package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.domain.Prompt;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    // 내 프롬프트 최신순
    List<Prompt> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 카테고리별 페이지 조회
    Page<Prompt> findByCategoryId(Long categoryId, Pageable pageable);

    // 카테고리 삭제
    long countByCategory(com.tave.PromptMate.domain.Category category);

    // 전체 프롬프트 목록 조회 - 최신순
    List<Prompt> findAllByOrderByCreatedAtDesc();

    // 권한 체크 (내가 쓴 프롬프트인가)
    Optional<Prompt> findByIdAndUserId(Long id, Long userId);

    List<Prompt> findAllByCategoryOrderByCreatedAtDesc(Category category);

    Page<Prompt> findByDeletedFalse(Pageable pageable);
    Page<Prompt> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);
    Page<Prompt> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);
    Page<Prompt> findByDeletedFalseAndTitleContainingIgnoreCaseOrDeletedFalseAndContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable);

    // 존재 확인
    boolean existsByIdAndDeletedFalse(Long id);

    // 검색 - 제목/내용에 키워드 포함 (최신순)
    @Query("""
        SELECT p
        FROM Prompt p
        WHERE p.deleted = false
          AND (
                LOWER(COALESCE(p.title,   '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(COALESCE(p.content, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY p.createdAt DESC
    """)
    Page<Prompt> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}