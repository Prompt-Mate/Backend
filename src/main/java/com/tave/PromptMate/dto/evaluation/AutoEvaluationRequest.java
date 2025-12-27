package com.tave.PromptMate.dto.evaluation;

import jakarta.validation.constraints.NotNull;

public record AutoEvaluationRequest(
        @NotNull Long promptId,
        @NotNull Long rewriteId
) {}
