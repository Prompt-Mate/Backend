package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;

public final class EvaluationMapper {
    private EvaluationMapper(){}

    public static Evaluation toEntity(CreateEvaluationRequest req, Prompt p, RewriteResult r) {
        return Evaluation.builder()
                .prompt(p)
                .rewriteResult(r)
                .clarity(req.clarity())
                .specificity(req.specificity())
                .context(req.context())
                .creativity(req.creativity())
                .advice(req.advice())
                .model_name(req.modelName())
                .build();
    }

    public static EvaluationResponse toResponse(Evaluation e) {
        return new EvaluationResponse(
                e.getId(),
                e.getPrompt().getId(),
                e.getRewriteResult().getId(),
                e.getClarity(),
                e.getSpecificity(),
                e.getContext(),
                e.getCreativity(),
                e.getAdvice(),
                e.getModel_name()
        );
    }
}
