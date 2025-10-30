package com.tave.PromptMate.dto.rewrite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateRewriteRequest(
        @NotNull Long id,
        @NotNull Long promptId,
        @NotBlank String modelName,
        @NotBlank String content,
        @NotNull Integer inputTokens,
        @NotNull Integer outputTokens,
        @NotNull Long latencyMs,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
