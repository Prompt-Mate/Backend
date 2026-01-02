package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Library;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryMapper;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final RewriteResultRepository rewriteResultRepository;
    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeService communityLikeService;
    private final CommentRepository commentRepository;

    // 라이브러리에 리라이팅 결과 저장하기
    public LibraryResponse save(CreateLibraryRequest req, Long userId) {
        if (libraryRepository.existsByUser_IdAndRewriteResult_Id(userId, req.rewriteResultId())){
            throw new IllegalStateException("이미 라이브러리에 저장된 결과입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found: " + userId));

        RewriteResult result = rewriteResultRepository.findById(req.rewriteResultId())
                .orElseThrow(() -> new NotFoundException("rewrite result not found: " + req.rewriteResultId()));

        Library library = Library.builder()
                .user(user)
                .prompt(result.getPrompt())
                .rewriteResult(result)
                .savedTitle(req.savedTitle())
                .build();

        Library saved = libraryRepository.save(library);
        return LibraryMapper.toResponse(saved);
    }

    // 내 라이브러리 목록 조회하기
    @Transactional(readOnly = true)
    public List<LibraryResponse> getMyLibraries(Long userId){
        return libraryRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LibraryMapper::toResponse)
                .toList();
    }

    // 단건 조회
    @Transactional
    public LibraryResponse getOne(Long id, Long userId){
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        return LibraryMapper.toResponse(lib);
    }

    // 삭제하기
    public void delete(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        libraryRepository.delete(lib);
    }

    //내가 작성한 게시글 삭제
    public void deletePost(Long postId, Long userId){
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다. id=" + postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        post.remove();

        // postId로 정확히 해당 라이브러리만 삭제
        //libraryRepository.findByCommunity_Id(postId)
          //      .ifPresent(libraryRepository::delete);

    }

    //내가 작성한 커뮤니티 글 조회
    public List<CommunityPostResponse> getMyPosts(Long userId){

        List<Community> posts=communityRepository
                .findByUser_IdAndVisibilityNotOrderByCreatedAtDesc(
                        userId,
                        Community.Visibility.REMOVED
                );
        return posts.stream()
                .map(post->{

                    long likeCount= communityLikeService.getLikeCount(post.getId());
                    long commentCount=commentRepository.countByCommunityId(post.getId());
                    boolean isLiked= communityLikeService.isLiked(post.getId(), userId);

                    return CommunityPostMapper.toResponse(post,likeCount,commentCount,isLiked);
                })
                .toList();
    }
}