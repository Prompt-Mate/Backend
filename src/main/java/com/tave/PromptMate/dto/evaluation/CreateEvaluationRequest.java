package com.tave.PromptMate.dto.evaluation;

import jakarta.validation.constraints.NotNull;

public record CreateEvaluationRequest(
        @NotNull Long promptId,
        @NotNull Long rewriteId,
        Integer overallScore,
        Integer clarityScore,
        Integer specificityScore,
        Integer structureScore,
        Integer languageScore,
        Integer consistencyScore,

        String clarityComment,
        String specificityComment,
        String structureComment,
        String languageComment,
        String consistencyComment,

        String summaryFeedback
) {
}
