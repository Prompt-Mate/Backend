package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.*;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.community.CreateCommunityPostRequest;
import com.tave.PromptMate.dto.community.UpdateCommunityPostRequest;
import com.tave.PromptMate.repository.CategoryRepository;
import com.tave.PromptMate.repository.CommunityRepository;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final CategoryRepository categoryRepository;

    public CommunityPostResponse createPost(CreateCommunityPostRequest req, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. id=" + req.categoryId()));

        // 1) 프롬프트 생성
        Prompt prompt = promptRepository.findById(req.promptId())
                .orElseThrow(()->new NotFoundException("존재하지 않는 프롬프트입니다.id=" + req.promptId()));

        Prompt savedPrompt = promptRepository.save(prompt);

        String promptContent= prompt.getContent();

        // 2) 커뮤니티 엔티티 생성
        Community community = Community.create(
                user,
                prompt,
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