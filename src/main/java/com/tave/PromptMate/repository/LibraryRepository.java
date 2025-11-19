package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 특정 유저 라이브러리 검색
    @Query(
            value = """
                    SELECT 
                        l.id,
                        l.user_id,
                        l.prompt_id,
                        l.rewrite_result_id,
                        l.saved_title,
                        l.created_at
                    FROM `library` l
                    WHERE l.user_id = :userId
                      AND l.saved_title LIKE CONCAT('%', :keyword, '%')
                    ORDER BY l.created_at DESC
                    """,
            nativeQuery = true
    )
    List<Library> searchMyLibrariesNative(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
}
