package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.domain.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    // 내 프롬프트 최신순
    List<Prompt> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 카테고리별 페이지 조회
    Page<Prompt> findByCategoryId(Long categoryId, Pageable pageable);

    // 카테고리 삭제
    long countByCategory(com.tave.PromptMate.domain.Category category);

    // 전체 프롬프트 목록 조회
    List<Prompt> findAllByOrderByCreatedAtDesc();

    // 권한 체크 (내가 쓴 프롬프트인가)
    Optional<Prompt> findByIdAndUserId(Long id, Long userId);

    List<Prompt> findAllByCategoryOrderByCreatedAtDesc(Category category);

    Page<Prompt> findByDeletedFalse(Pageable pageable);
    Page<Prompt> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);
    Page<Prompt> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);
    Page<Prompt> findByDeletedFalseAndTitleContainingIgnoreCaseOrDeletedFalseAndContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable);
    boolean existsByIdAndDeletedFalse(Long id);
}
