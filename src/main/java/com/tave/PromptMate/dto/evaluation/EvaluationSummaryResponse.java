package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.HighlightsPayload;

import java.time.LocalDateTime;

public record EvaluationSummaryResponse(
        Long promptId,
        Double clarityAvg,
        Double specificityAvg,
        Double contextAvg,
        Double creativityAvg,
        Double totalScoreAvg,
        String latestSummary,
        HighlightsPayload latestHighlights,
        String latestModelName,
        String latestAdvice,
        LocalDateTime latestCreatedAt
) {
}
