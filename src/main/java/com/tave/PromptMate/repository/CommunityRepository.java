package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Community.Visibility;
import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;
import com.tave.PromptMate.dto.community.CommunityPostRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 내가 작성한 글 조회
    List<Community> findByUser_IdAndVisibilityNotOrderByCreatedAtDesc(
            Long userId,
            Visibility visibility
    );

    // 1) 최신순
    @Query("""
    select new com.tave.PromptMate.dto.community.CommunityPostRow(
        c.id,
        rr.id,
        u.id,
        u.nickname,
        c.title,
        c.promptContent,
        c.visibility,
        c.description,
        c.createdAt,
        c.viewCount,
        (select count(cl) from CommunityLike cl where cl.community.id = c.id),
        (select count(cm) from Comment cm where cm.community.id = c.id),
        (case when :userId is null then false
              when (select count(cl2) from CommunityLike cl2
                    where cl2.community.id = c.id and cl2.user.id = :userId) > 0
              then true else false end),
        c.platform,
        c.category,
        c.imageUrl
    )
    from Community c
    join c.rewriteResult rr
    join c.user u
    where c.visibility <> com.tave.PromptMate.domain.Community.Visibility.REMOVED
      and (:q is null
           or c.title like concat('%', :q, '%')
           or c.promptContent like concat('%', :q, '%'))
      and (:platform is null or c.platform = :platform)
      and (:category is null or c.category = :category)
    order by c.createdAt desc
    """)
    List<CommunityPostRow> findAllLatest(
            @Param("q") String q,
            @Param("platform") Platform platform,
            @Param("category") PromptCategory category,
            @Param("userId") Long userId
    );

    // 2) 조회순
    @Query("""
    select new com.tave.PromptMate.dto.community.CommunityPostRow(
        c.id,
        rr.id,
        u.id,
        u.nickname,
        c.title,
        c.promptContent,
        c.visibility,
        c.description,
        c.createdAt,
        c.viewCount,
        (select count(cl) from CommunityLike cl where cl.community.id = c.id),
        (select count(cm) from Comment cm where cm.community.id = c.id),
        (case when :userId is null then false
              when (select count(cl2) from CommunityLike cl2
                    where cl2.community.id = c.id and cl2.user.id = :userId) > 0
              then true else false end),
        c.platform,
        c.category,
        c.imageUrl
    )
    from Community c
    join c.rewriteResult rr
    join c.user u
    where c.visibility <> com.tave.PromptMate.domain.Community.Visibility.REMOVED
      and (:q is null
           or c.title like concat('%', :q, '%')
           or c.promptContent like concat('%', :q, '%'))
      and (:platform is null or c.platform = :platform)
      and (:category is null or c.category = :category)
    order by c.viewCount desc, c.createdAt desc
    """)
    List<CommunityPostRow> findAllView(
            @Param("q") String q,
            @Param("platform") Platform platform,
            @Param("category") PromptCategory category,
            @Param("userId") Long userId
    );

    // 3) 좋아요순
    @Query("""
    select new com.tave.PromptMate.dto.community.CommunityPostRow(
        c.id,
        rr.id,
        u.id,
        u.nickname,
        c.title,
        c.promptContent,
        c.visibility,
        c.description,
        c.createdAt,
        c.viewCount,
        (select count(cl) from CommunityLike cl where cl.community.id = c.id),
        (select count(cm) from Comment cm where cm.community.id = c.id),
        (case when :userId is null then false
              when (select count(cl2) from CommunityLike cl2
                    where cl2.community.id = c.id and cl2.user.id = :userId) > 0
              then true else false end),
        c.platform,
        c.category,
        c.imageUrl
    )
    from Community c
    join c.rewriteResult rr
    join c.user u
    where c.visibility <> com.tave.PromptMate.domain.Community.Visibility.REMOVED
      and (:q is null
           or c.title like concat('%', :q, '%')
           or c.promptContent like concat('%', :q, '%'))
      and (:platform is null or c.platform = :platform)
      and (:category is null or c.category = :category)
    order by
        (select count(cl) from CommunityLike cl where cl.community.id = c.id) desc,
        c.createdAt desc
    """)
    List<CommunityPostRow> findAllLike(
            @Param("q") String q,
            @Param("platform") Platform platform,
            @Param("category") PromptCategory category,
            @Param("userId") Long userId
    );

    // 좋아요순 인기 프롬프트
    @Query("""
select new com.tave.PromptMate.dto.community.CommunityPostRow(
    c.id,
    rr.id,
    u.id,
    u.nickname,
    c.title,
    c.promptContent,
    c.visibility,
    c.description,
    c.createdAt,
    c.viewCount,
    (select count(cl) from CommunityLike cl where cl.community.id = c.id),
    (select count(cm) from Comment cm where cm.community.id = c.id),
    (case when :userId is null then false
          when (select count(cl2) from CommunityLike cl2
                where cl2.community.id = c.id and cl2.user.id = :userId) > 0
          then true else false end),
    c.platform,
    c.category,
    c.imageUrl
)
from Community c
join c.rewriteResult rr
join c.user u
where c.visibility <> com.tave.PromptMate.domain.Community.Visibility.REMOVED
order by
    (select count(cl) from CommunityLike cl where cl.community.id = c.id) desc,
    c.createdAt desc
""")
    List<CommunityPostRow> findPopularTop(
            @Param("userId") Long userId,
            Pageable pageable
    );

}