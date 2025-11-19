package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // 특정 리라이팅 결과에 대한 모든 평가
    List<Evaluation> findAllByRewriteResultId(Long rewriteId);

    // 프롬프트+리라이팅 조합으로 단건 조회
    Optional<Evaluation> findByPromptIdAndRewriteResultId(Long promptId, Long rewriteId);

    // 특정 프롬프트에 달린 평가들
    List<Evaluation> findAllByPromptOrderByIdDesc(Prompt prompt);

    // 특정 리라이팅 결과에 달린 평가들
    List<Evaluation> findAllByRewriteResultOrderByIdDesc(RewriteResult rewriteResult);

    // 프롬프트별 평가 - 페이징
    Page<Evaluation> findByPromptId(Long promptId, Pageable pageable);

    // 리라이트 결과별 평가 - 페이징
    Page<Evaluation> findByRewriteResultId(Long rewriteId, Pageable pageable);

   // 프롬프트별 평균 점수
   @Query("""
    SELECT 
        AVG(e.clarity),
        AVG(e.specificity),
        AVG(e.context),
        AVG(e.creativity),
        AVG(e.totalScore)
    FROM Evaluation e
    WHERE e.prompt.id = :promptId
""")
   Object[] avgByPromptId(@Param("promptId") Long promptId);


   // 프롬프트별 가장 최근 평가 1건
   @Query("""
        select e from Evaluation e
        where e.prompt.id = :promptId
        order by e.id desc
    """)
   Page<Evaluation> findLatestOneByPromptId(@Param("promptId") Long promptId, Pageable pageable);



    // 프롬프트 최신 1건 (요약/하이라이트 카드용)
    Optional<Evaluation> findTopByPromptIdOrderByIdDesc(Long promptId);

    //리라이팅 결과별 최신 1건 (라이브러리 카드용)
    Optional<Evaluation> findTopByRewriteResultIdOrderByIdDesc(Long rewriteId);
}