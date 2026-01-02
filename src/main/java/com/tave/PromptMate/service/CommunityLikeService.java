package com.tave.PromptMate.service;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.CommunityLike;
import com.tave.PromptMate.domain.User;
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
    public void like(Long communityId, Long userId){

        // 게시글 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(()->new IllegalStateException("게시글이 존재하지 않습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalStateException("사용자가 존재하지 않습니다."));

        // 이미 좋아요 눌렀으면 그냥 종료
        if (communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId)){
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
    public long getLikeCount(Long communityId){
        return communityLikeRepository.countByCommunityId(communityId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long communityId, Long userId){
        if(userId==null) return false;
        return communityLikeRepository.existsByUserIdAndCommunityId(userId, communityId);
    }
}

