package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.*;
import com.tave.PromptMate.dto.community.*;
import com.tave.PromptMate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final RewriteResultRepository rewriteResultRepository;
    private final PromptRepository promptRepository;
    private final CategoryRepository categoryRepository;
    private final LibraryRepository libraryRepository;
    private final CommentRepository commentRepository;
    private final CommunityLikeService communityLikeService;

    public CommunityPostResponse createPost(CreateCommunityPostRequest req, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. id=" + req.categoryId()));

        // 1) 리라이팅 결과 조회
        RewriteResult rewriteResult= rewriteResultRepository.findById(req.rewriteResultId())
                .orElseThrow(()->new NotFoundException("존재하지 않는 리라이팅 결과입니다. id="+req.rewriteResultId()));

        Prompt prompt = rewriteResult.getPrompt();

        // 2) 글 작성 시 프롬프트 내용=리라이팅 결과
        String promptContent= rewriteResult.getContent();


        // 3) 커뮤니티 엔티티 생성
        Community community = Community.create(
                user,
                prompt,
                rewriteResult,
                category,
                req.title(),
                req.description(),
                promptContent,
                req.visibility()
        );

        Community savedCommunity = communityRepository.save(community);

        Library library = Library.builder()
                .user(user)
                .prompt(prompt)
                .rewriteResult(rewriteResult)
                .community(savedCommunity)
                .savedTitle(req.title())  // 커뮤니티 글 제목을 라이브러리 제목으로 사용
                .build();
        libraryRepository.save(library);

        long likeCount= 0L;
        long commentCount=0L;
        boolean isLiked=false;

        return CommunityPostMapper.toResponse(savedCommunity, likeCount, commentCount, isLiked);


    }

    @Transactional
    public CommunityPostResponse updatePost(Long postId, UpdateCommunityPostRequest req, Long userId){
        Community post=communityRepository.findById(postId)
                .orElseThrow(()->new NotFoundException("존재하지 않는 게시글입니다. id="+postId));

        if(post.getVisibility()==Community.Visibility.REMOVED){
            throw new NotFoundException("삭제된 게시글입니다.");
        }

        //작성자 검증
        if(!post.getUser().getId().equals(userId)){
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        //커뮤니티 수정
        post.update(req.title(), req.description(), req.visibility());

        long likeCount= communityLikeService.getLikeCount(post.getId());
        long commentCount=commentRepository.countByCommunityId(post.getId());
        boolean isLiked= communityLikeService.isLiked(post.getId(), userId);

        return CommunityPostMapper.toResponse(post, likeCount, commentCount, isLiked);
    }

    public void deletePost(Long postId, Long userId){
        Community post=communityRepository.findById(postId)
                .orElseThrow(()->new NotFoundException("존재하지 않는 게시글입니다. id=" + postId));

        if(!post.getUser().getId().equals(userId)){
            throw new IllegalStateException("삭제 권한이 없습니다. ");
        }

        post.remove();

        // 라이브러리에서도 삭제
        libraryRepository.findByCommunity_Id(postId).ifPresent(libraryRepository::delete);

    }
    // 4) 게시글 목록 조회(최신순/조회순/좋아요순)
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getPosts(String sort, Long userId) {

        String s = (sort == null) ? "latest" : sort;

        List<CommunityPostRow> rows = switch (s) {
            case "latest" -> communityRepository.findAllLatest(userId);
            case "view" -> communityRepository.findAllView(userId);
            case "like" -> communityRepository.findAllLike(userId);
            default -> communityRepository.findAllLatest(userId);
        };

        return rows.stream()
                .map(r -> new CommunityPostResponse(
                        r.id(),
                        r.rewriteResultId(),
                        r.userId(),
                        r.nickname(),
                        r.categoryId(),
                        r.categoryName(),
                        r.title(),
                        r.promptContent(),
                        r.visibility(),
                        r.createdAt(),
                        r.viewCount(),
                        r.likeCount(),
                        r.commentCount(),
                        r.isLiked()
                ))
                .toList();
    }

    // 5) 게시글 단건 조회
    @Transactional
    public CommunityPostResponse getPost(Long postId, Long userId) {

        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다. id=" + postId));

        if (post.getVisibility() == Community.Visibility.REMOVED) {
            throw new NotFoundException("삭제된 게시글입니다.");
        }

        post.increaseViewCount();

        long likeCount = communityLikeService.getLikeCount(postId);
        long commentCount=commentRepository.countByCommunityId(postId);
        boolean isLiked = communityLikeService.isLiked(postId, userId);

        return CommunityPostMapper.toResponse(post, likeCount, commentCount, isLiked);
    }
}
