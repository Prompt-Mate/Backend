package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.CommunityRecent;
import com.tave.PromptMate.dto.community.CommunityRecentRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRecentRepository extends JpaRepository<CommunityRecent,Long> {

    // 이미 기록이 있는지 확인
    Optional<CommunityRecent> findByUser_IdAndCommunity_Id(Long userId, Long communityId);

    @Query("""
        select new com.tave.PromptMate.dto.community.CommunityRecentRow(
            c.id,
            c.title,
            c.platform,
            c.category,
            r.lastViewedAt
        )
        from CommunityRecent r
        join r.community c
        where r.user.id = :userId
          and c.visibility <> com.tave.PromptMate.domain.Community.Visibility.REMOVED
        order by r.lastViewedAt desc
    """)
    List<CommunityRecentRow> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
}

