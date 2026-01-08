package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    // 특정 유저 라이브러리 목록 - 최신순
    List<Library> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // 특정 유저의 단건 조회
    Optional<Library> findByIdAndUser_Id(Long id, Long userId);

    // 중복 저장 방지
    boolean existsByUser_IdAndRewriteResult_Id(Long userId, Long rewriteResultId);

    Optional<Library> findByUser_IdAndRewriteResult_Id(Long userId, Long rewriteResultId);

    Optional<Library> findByCommunity_Id(Long communityId);

    // 내 라이브러리 검색필터
    @Query("""
        select l
        from Library l
        where l.user.id = :userId
          and (:keyword is null or :keyword = '' or l.savedTitle like %:keyword%)
          and (:platform is null or :platform = '' or l.platform = :platform)
          and (:category is null or :category = '' or l.category = :category)
    """)
    Page<Library> searchMyLibraries(
            Long userId,
            String keyword,
            String platform,
            String category,
            Pageable pageable
    );
}