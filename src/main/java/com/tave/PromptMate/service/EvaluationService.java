package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.dto.evaluation.CreateEvaluationRequest;
import com.tave.PromptMate.dto.evaluation.EvaluationMapper;
import com.tave.PromptMate.dto.evaluation.EvaluationResponse;
import com.tave.PromptMate.dto.evaluation.EvaluationSummaryResponse;
import com.tave.PromptMate.repository.EvaluationRepository;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.RewriteResultRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class EvaluationService {

    private final UserRepository userRepository;
    private final PromptRepository promptRepository;
    private final RewriteResultRepository rewriteResultRepository;
    private final EvaluationRepository evaluationRepository;

    // 평가 생성하기
    public EvaluationResponse createEvaluation(CreateEvaluationRequest req){
        Prompt prompt = promptRepository.findById(req.promptId())
                .orElseThrow(()-> new NotFoundException("prompt not found: "+req.promptId()));
        RewriteResult rewrite = rewriteResultRepository.findById(req.rewriteId())
                .orElseThrow(()->new NotFoundException("rewrite result not found: "+req.rewriteId()));

        Evaluation saved = evaluationRepository.save(
                EvaluationMapper.toEntity(req, prompt, rewrite)
        );
        return EvaluationMapper.toResponse(saved);
    }

    // 프롬프트별 평가 목록
    @Transactional(readOnly = true)
    public Page<EvaluationResponse> getEvaluationsByPromptPage(Long promptId, int page, int size){
        promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return evaluationRepository.findByPromptId(promptId, pageable)
                .map(EvaluationMapper::toResponse);
    }

    // 리라이트 결과별 평가 목록
    @Transactional(readOnly = true)
    public Page<EvaluationResponse> getEvaluationsByRewritePage(Long rewriteId, int page, int size){
        rewriteResultRepository.findById(rewriteId)
                .orElseThrow(() -> new NotFoundException("rewrite result not found: " + rewriteId));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return evaluationRepository.findByRewriteResultId(rewriteId, pageable)
                .map(EvaluationMapper::toResponse);
    }

    // 평가 삭제하기
    public void deleteEvaluation(Long evaluationId){
        evaluationRepository.deleteById(evaluationId);
    }

    // 평가 단건 조회
    @Transactional(readOnly = true)
    public EvaluationResponse getById(Long evaluationId){
        Evaluation e = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new NotFoundException("evaluation not found: " + evaluationId));
        return EvaluationMapper.toResponse(e);
    }

    // 평가 요약
    @Transactional(readOnly = true)
    public EvaluationSummaryResponse getSummaryByPrompt(Long promptId) {
        // 프롬프트 존재 확인
        promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));

        // 평균값 계산
        Object[] avg = evaluationRepository.avgByPromptId(promptId);
        Double clarityAvg = null, specificityAvg = null, contextAvg = null, creativityAvg = null, totalAvg = null;
        if (avg != null && avg.length == 5) {
            clarityAvg     = castToDouble(avg[0]);
            specificityAvg = castToDouble(avg[1]);
            contextAvg     = castToDouble(avg[2]);
            creativityAvg  = castToDouble(avg[3]);
            totalAvg       = castToDouble(avg[4]);
        }

        // 최신 1건 조회 (람다 대신 if/else)
        var latestOpt = evaluationRepository.findTopByPromptIdOrderByIdDesc(promptId);
        if (latestOpt.isPresent()) {
            var latest = latestOpt.get();
            return new EvaluationSummaryResponse(
                    promptId,
                    clarityAvg, specificityAvg, contextAvg, creativityAvg, totalAvg,
                    latest.getSummary(),
                    latest.getHighlights(),
                    latest.getModelName(),
                    latest.getAdvice(),
                    latest.getCreatedAt()
            );
        } else {
            return new EvaluationSummaryResponse(
                    promptId,
                    clarityAvg, specificityAvg, contextAvg, creativityAvg, totalAvg,
                    null, null, null, null, null
            );
        }
    }

    private Double castToDouble(Object o) {
        return (o == null) ? null : ((Number) o).doubleValue();
    }
}