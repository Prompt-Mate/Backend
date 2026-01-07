package com.tave.PromptMate.service;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.CommunityLike;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.community.CommunityLikeToggleResponse;
import com.tave.PromptMate.repository.CommunityLikeRepository;
import com.tave.PromptMate.repository.CommunityRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityLikeService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Transactional
    public CommunityLikeToggleResponse toggleLike(Long communityId, Long userId) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        boolean alreadyLiked =
                communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId);

        if (alreadyLiked) {
            communityLikeRepository.deleteByUserIdAndCommunityId(userId, communityId);
        } else {
            communityLikeRepository.save(CommunityLike.of(user, community));
        }

        long likeCount = communityLikeRepository.countByCommunityId(communityId);
        return new CommunityLikeToggleResponse(!alreadyLiked, likeCount);
    }

    @Transactional
    public void like(Long communityId, Long userId) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        if (communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId)) {
            return;
        }
        communityLikeRepository.save(CommunityLike.of(user, community));
    }

    @Transactional
    public void unlike(Long communityId, Long userId) {
        if (!communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId)) {
            return;
        }
        communityLikeRepository.deleteByUserIdAndCommunityId(userId, communityId);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long communityId) {
        return communityLikeRepository.countByCommunityId(communityId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long communityId, Long userId) {
        if (userId == null) return false;
        return communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId);
    }
}
