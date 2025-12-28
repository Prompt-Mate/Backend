package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.*;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.community.CreateCommunityPostRequest;
import com.tave.PromptMate.dto.community.UpdateCommunityPostRequest;
import com.tave.PromptMate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final RewriteResultRepository rewriteResultRepository;
    private final PromptRepository promptRepository;
    private final CategoryRepository categoryRepository;

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

        return CommunityPostMapper.toResponse(savedCommunity);
    }
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

        return CommunityPostMapper.toResponse(post);
    }

    public void deletePost(Long postId, Long userId){
        Community post=communityRepository.findById(postId)
                .orElseThrow(()->new NotFoundException("존재하지 않는 게시글입니다. id=" + postId));

        if(!post.getUser().getId().equals(userId)){
            throw new IllegalStateException("삭제 권한이 없습니다. ");
        }

        post.remove();
    }
}