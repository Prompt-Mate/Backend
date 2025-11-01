package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.dto.evaluation.CreateEvaluationRequest;
import com.tave.PromptMate.dto.evaluation.EvaluationResponse;
import com.tave.PromptMate.repository.EvaluationRepository;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.RewriteResultRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tave.PromptMate.dto.evaluation.EvaluationMapper.toResponse;

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

        Evaluation evaluation = Evaluation.builder()
                .prompt(prompt)
                .rewriteResult(rewrite)
                .clarity(req.clarity())
                .specificity(req.specificity())
                .context(req.context())
                .creativity(req.creativity())
                .advice(req.advice())
                .model_name(req.modelName())
                .build();
        Evaluation saved = evaluationRepository.save(evaluation);
        return toResponse(saved);
    }

    // 프롬프트별 평가 목록
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByPrompt(Long promptId){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(()->new NotFoundException("prompt not found: "+promptId));
        return evaluationRepository.findAllByPromptOrderByIdDesc(prompt)
                .stream().map(e -> new EvaluationResponse(
                        e.getId(),
                        e.getPrompt().getId(),
                        e.getRewriteResult().getId(),
                        e.getClarity(),
                        e.getSpecificity(),
                        e.getContext(),
                        e.getCreativity(),
                        e.getAdvice(),
                        e.getModel_name()
                )).toList();
    }

    // 리라이트 결과별 평가 목록
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByRewrite(Long rewriteId) {
        RewriteResult rewrite = rewriteResultRepository.findById(rewriteId)
                .orElseThrow(() -> new NotFoundException("rewrite result not found: " + rewriteId));

        return evaluationRepository.findAllByRewriteResultOrderByIdDesc(rewrite)
                .stream()
                .map(e -> new EvaluationResponse(
                        e.getId(),
                        e.getPrompt().getId(),
                        e.getRewriteResult().getId(),
                        e.getClarity(),
                        e.getSpecificity(),
                        e.getContext(),
                        e.getCreativity(),
                        e.getAdvice(),
                        e.getModel_name()
                )).toList();
    }

    // 평가 삭제하기
    public void deleteEvaluation(Long evaluationId){
        evaluationRepository.deleteById(evaluationId);
    }
}
