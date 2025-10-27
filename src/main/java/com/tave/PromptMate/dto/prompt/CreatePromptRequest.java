package com.tave.PromptMate.dto.prompt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreatePromptRequest(
        @NotNull Long userId,
        Long categoryId,
        @NotBlank String title,
        @NotBlank String content,
        Boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}