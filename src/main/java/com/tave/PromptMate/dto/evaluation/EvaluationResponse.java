package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.HighlightsPayload;

public record EvaluationResponse(
        Long id,
        Long promptId,
        Long rewriteId,
        Integer clarity,
        Integer specificity,
        Integer context,
        Integer creativity,
        Integer totalScore,
        String advice,
        String modelName,
        String summary,
        HighlightsPayload highlights
) {
}
