package com.tave.PromptMate.dto.evaluation;

public record EvaluationResponse(
        Long id,
        Long promptId,
        Long rewriteResultId,

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
