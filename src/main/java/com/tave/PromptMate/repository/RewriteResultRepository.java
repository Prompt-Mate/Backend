package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.RewriteResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewriteResultRepository extends JpaRepository<RewriteResult, Long> {

    // 최신 리라이팅 1건
    Optional<RewriteResult> findTopByPromptIdOrderByCreatedAtDesc(Long promptId);

    // 프롬프트별 전체 리라이팅 (최신순)
    List<RewriteResult> findByPromptIdOrderByCreatedAtDesc(Long promptId);
}
