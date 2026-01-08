package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    boolean existsByUserIdAndCommunityId(Long userId, Long communityId);

    long countByCommunityId(Long communityId);

    void deleteByUserIdAndCommunityId(Long userId, Long communityId);

    void deleteByCommunityId(Long communityId);
}
