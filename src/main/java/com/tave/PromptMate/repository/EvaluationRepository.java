package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // 특정 리라이팅 결과에 대한 모든 평가
    List<Evaluation> findAllByRewriteResultId(Long rewriteId);

    // 프롬프트+리라이팅 조합으로 단건 조회
    Optional<Evaluation> findByPromptIdAndRewriteResultId(Long promptId, Long rewriteId);

    List<Evaluation> findAllByPromptOrderByIdDesc(Prompt prompt);
    List<Evaluation> findAllByRewriteResultOrderByIdDesc(RewriteResult rewriteResult);
}