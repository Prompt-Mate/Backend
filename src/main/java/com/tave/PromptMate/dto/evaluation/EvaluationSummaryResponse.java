package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.HighlightsPayload;

import java.time.LocalDateTime;

public record EvaluationSummaryResponse(
        Long promptId,
        Double clarityAvg,
        Double specificityAvg,
        Double structureAvg,
        Double languageAvg,
        Double consistencyAvg,
        Double overallAvg,
        String latestSummary,
        HighlightsPayload latestHighlights,
        String latestModelName,
        LocalDateTime latestCreatedAt

        ) {
}
