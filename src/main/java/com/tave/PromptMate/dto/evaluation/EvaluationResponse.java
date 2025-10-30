package com.tave.PromptMate.dto.evaluation;

public record EvaluationResponse(
        Long id,
        Long promptId,
        Long rewriteId,
        Integer clarity,
        Integer specificity,
        Integer context,
        Integer creativity,
        String advice,
        String modelName
) {
}
