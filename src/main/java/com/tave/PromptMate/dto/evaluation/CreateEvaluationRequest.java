package com.tave.PromptMate.dto.evaluation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEvaluationRequest(
        @NotNull Long promptId,
        @NotNull Long rewriteId,
        @NotNull @Min(0) @Max(10) Integer clarity,
        @NotNull @Min(0) @Max(10) Integer specificity,
        @NotNull @Min(0) @Max(10) Integer context,
        @NotNull @Min(0) @Max(10) Integer creativity,
        @NotBlank String advice,
        @NotBlank String modelName
) {
}
