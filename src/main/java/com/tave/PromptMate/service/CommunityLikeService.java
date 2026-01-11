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
    public CommunityLikeToggleResponse toggleLike(Long postId, Long userId) {

        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        boolean alreadyLiked =
                communityLikeRepository.existsByUserIdAndCommunityId(userId, postId);

        if (alreadyLiked) {
            communityLikeRepository.deleteByUserIdAndCommunityId(userId, postId);
        } else {
            communityLikeRepository.save(CommunityLike.of(user, community));
        }

        long likeCount = communityLikeRepository.countByCommunityId(postId);
        return new CommunityLikeToggleResponse(!alreadyLiked, likeCount);
    }

    @Transactional
    public void like(Long postId, Long userId) {

        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));

        if (communityLikeRepository.existsByUserIdAndCommunityId(userId, postId)) {
            return;
        }
        communityLikeRepository.save(CommunityLike.of(user, community));
    }

    @Transactional
    public void unlike(Long postId, Long userId) {
        if (!communityLikeRepository.existsByUserIdAndCommunityId(userId, postId)) {
            return;
        }
        communityLikeRepository.deleteByUserIdAndCommunityId(userId, postId);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        return communityLikeRepository.countByCommunityId(postId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        if (userId == null) return false;
        return communityLikeRepository.existsByUserIdAndCommunityId(userId, postId);
    }

    @Transactional
    public void deleteAllByPostId(Long postId){
        communityLikeRepository.deleteByCommunityId(postId);
    }
}
