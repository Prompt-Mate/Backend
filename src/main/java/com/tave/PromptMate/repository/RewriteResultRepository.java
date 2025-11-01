package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewriteResultRepository extends JpaRepository<RewriteResult, Long> {

    // 특정 프롬프트의 리라이팅 결과들(최신순)
    List<RewriteResult> findAllByPromptIdOrderByCreatedAtDesc(Long promptId);

    // 가장 최근 리라이팅 결과 1건
    Optional<RewriteResult> findTop1ByPromptIdOrderByCreatedAtDesc(Long promptId);
    void deleteAllByPrompt(Prompt prompt);
}
