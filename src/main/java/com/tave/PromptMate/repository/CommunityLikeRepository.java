package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.CommunityLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    boolean existsByUserIdAndCommunityId(Long userId, Long communityId);

    long countByCommunityId(Long communityId);

    void deleteByUserIdAndCommunityId(Long userId, Long communityId);

    void deleteByCommunityId(Long communityId);

    @Query("select cl.community.id from CommunityLike cl where cl.user.id = :userId")
    Page<Long> findLikedCommunityIds(@Param("userId") Long userId, Pageable pageable);

}
