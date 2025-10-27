package com.tave.PromptMate.dto.prompt;

import java.time.LocalDateTime;

public record PromptResponse(
        Long id,
        Long userId,
        Long categoryId,
        String title,
        String content,
        Boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
