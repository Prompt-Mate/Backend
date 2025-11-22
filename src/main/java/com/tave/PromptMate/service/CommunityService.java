package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.*;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.community.CreateCommunityPostRequest;
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

    public CommunityPostResponse createPost(CreateCommunityPostRequest req) {

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다. id=" + req.userId()));

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. id=" + req.categoryId()));

        // 1) 프롬프트 생성
        Prompt prompt = Prompt.builder()
                .user(user)
                .category(category)
                .title(req.title())
                .content(req.promptContent())
                //.isPrivate(false)
                .build();

        Prompt savedPrompt = promptRepository.save(prompt);

        // 2) 커뮤니티 엔티티 생성
        Community community = Community.create(
                user,
                savedPrompt,
                category,
                req.visibility()
        );

        Community savedCommunity = communityRepository.save(community);

        return CommunityPostMapper.toResponse(savedCommunity);
    }
}
