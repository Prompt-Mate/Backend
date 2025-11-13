package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.HighlightsPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEvaluationRequest(
        @NotNull Long promptId,
        @NotNull Long rewriteId,
        @NotNull @Min(0) @Max(100) Integer clarity,
        @NotNull @Min(0) @Max(100) Integer specificity,
        @NotNull @Min(0) @Max(100) Integer context,
        @NotNull @Min(0) @Max(100) Integer creativity,
        @NotNull @Min(0) @Max(100) Integer totalScore,
        @NotBlank String advice,
        @NotBlank String modelName,
        @NotBlank String summary,
        @Valid HighlightsPayload highlights
) {
}
