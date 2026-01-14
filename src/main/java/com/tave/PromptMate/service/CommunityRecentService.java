package com.tave.PromptMate.service;


import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.CommunityRecent;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.community.CommunityRecentRow;
import com.tave.PromptMate.repository.CommunityRecentRepository;
import com.tave.PromptMate.repository.CommunityRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityRecentService {

    private final CommunityRecentRepository communityRecentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    // 최근 본 목록 조회
    @Transactional
    public List<CommunityRecentRow> getRecent(Long userId, int limit){
        return communityRecentRepository.findRecentByUserId(
                userId,
                PageRequest.of(0,limit)
        );
    }

    // 최근 본 프롬프트 저장/갱신
    @Transactional
    public void upsert(Long userId, Long communityId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다. id=" + communityId));

        communityRecentRepository.findByUser_IdAndCommunity_Id(userId, communityId)
                .ifPresentOrElse(
                        CommunityRecent::touch,
                        () -> communityRecentRepository.save(CommunityRecent.create(user, community)) // 없으면 생성
                );
    }
}
