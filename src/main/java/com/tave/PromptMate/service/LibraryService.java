package com.tave.PromptMate.service;

import com.tave.PromptMate.ai.domain.Judge;
import com.tave.PromptMate.ai.repository.JudgeRepository;
import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Library;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryDetailResponse;
import com.tave.PromptMate.dto.library.LibraryMapper;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final RewriteResultRepository rewriteResultRepository;
    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;
    private final JudgeRepository judgeRepository;


    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityLikeService communityLikeService;

    // 라이브러리에 리라이팅 결과 저장하기
    public LibraryResponse save(Long userId,CreateLibraryRequest req) {
        if (libraryRepository.existsByUser_IdAndRewriteResult_Id(userId, req.rewriteResultId())) {
            throw new IllegalStateException("이미 라이브러리에 저장된 결과입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found: " + userId));

        RewriteResult result = rewriteResultRepository.findById(req.rewriteResultId())
                .orElseThrow(() -> new NotFoundException("rewrite result not found: " + req.rewriteResultId()));

        Library library = Library.builder()
                .user(user)
                .rewriteResult(result)
                .savedTitle(req.savedTitle())
                .platform(req.platform())
                .category(req.category())
                .imageUrl(req.imageUrl())
                .build();

        return LibraryMapper.toResponse(libraryRepository.save(library));
    }

    // 내 라이브러리 목록 조회하기
    @Transactional(readOnly = true)
    public Page<LibraryResponse> getMyLibraries(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return libraryRepository.findByUser_Id(userId, pageable)
                .map(LibraryMapper::toResponse);
    }

    // 라이브러리 목록 상세 페이지 조회
    @Transactional(readOnly = true)
    public LibraryResponse getOne(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));

        return LibraryMapper.toResponse(lib);
    }

    // 라이브러리 상세 페이지 조회 (Judge 평가 포함)
    @Transactional(readOnly = true)
    public LibraryDetailResponse getDetail(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));

        RewriteResult rewriteResult = lib.getRewriteResult();

        // 원본 프롬프트 조회
        String originalPrompt = null;
        if (rewriteResult.getPrompt() != null) {
            originalPrompt = rewriteResult.getPrompt().getContent();
        }

        // Judge 평가 정보 조회
        LibraryDetailResponse.JudgeInfo judgeInfo = null;
        var judgeOpt = judgeRepository.findByRewriteResultId(rewriteResult.getId());
        if (judgeOpt.isPresent()) {
            Judge judge = judgeOpt.get();
            judgeInfo = new LibraryDetailResponse.JudgeInfo(
                    judge.getId(),
                    judge.getOverallScore(),
                    judge.getClarityScore(),
                    judge.getSpecificityScore(),
                    judge.getStructureScore(),
                    judge.getLanguageScore(),
                    judge.getConsistencyScore(),
                    judge.getClarityComment(),
                    judge.getSpecificityComment(),
                    judge.getStructureComment(),
                    judge.getLanguageComment(),
                    judge.getConsistencyComment(),
                    judge.getSummaryFeedback()
            );
        }

        return new LibraryDetailResponse(
                lib.getId(),
                lib.getUser().getId(),
                lib.getSavedTitle(),
                lib.getPlatform(),
                lib.getCategory(),
                lib.getImageUrl(),
                lib.getCreatedAt(),
                rewriteResult.getId(),
                rewriteResult.getContent(),
                originalPrompt,
                judgeInfo
        );
    }

    // 삭제하기
    public void delete(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        libraryRepository.delete(lib);
    }

    //내가 작성한 게시글 삭제
    public void deletePost(Long postId, Long userId) {
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다. id=" + postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        communityRepository.delete(post);

        // postId로 정확히 해당 라이브러리만 삭제
        libraryRepository.findByCommunity_Id(postId)
                .ifPresent(libraryRepository::delete);

    }

    //내가 작성한 커뮤니티 글 조회
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getMyPosts(Long userId) {
        List<Community> posts = communityRepository
                .findByUser_IdAndVisibilityNotOrderByCreatedAtDesc(
                        userId,
                        Community.Visibility.REMOVED
                );

        return posts.stream()
                .map(post -> {
                    long likeCount = communityLikeService.getLikeCount(post.getId());
                    long commentCount = commentRepository.countByCommunityId(post.getId());
                    boolean isLiked = communityLikeService.isLiked(post.getId(), userId);
                    return CommunityPostMapper.toResponse(post, likeCount, commentCount, isLiked);
                })
                .toList();
    }


    //내가 좋아요한 프롬프트 조회
    @Transactional(readOnly = true)
    public Page<CommunityPostResponse> getLikedPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Long> likedCommunityIds = communityLikeRepository.findLikedCommunityIds(userId, pageable);

        List<Long> ids = likedCommunityIds.getContent();
        List<Community> posts = communityRepository.findAllById(ids);

        Map<Long, Community> postMap = posts.stream()
                .collect(Collectors.toMap(Community::getId, p -> p));

        return likedCommunityIds.map(id -> {
            Community post = postMap.get(id);
            if (post == null) {
                throw new NotFoundException("community not found: " + id);
            }

            long likeCount = communityLikeService.getLikeCount(post.getId());
            long commentCount = commentRepository.countByCommunityId(post.getId());
            boolean isLiked = true;

            return CommunityPostMapper.toResponse(post, likeCount, commentCount, isLiked);
        });
    }

    // 검색 필터 페이징
    @Transactional(readOnly = true)
    public Page<LibraryResponse> searchMyLibraries(
            Long userId,
            String keyword,
            String platform,
            String category,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Library> libraries = libraryRepository.searchMyLibraries(
                userId,
                keyword,
                platform,
                category,
                pageable
        );

        return libraries.map(LibraryMapper::toResponse);
    }
}
