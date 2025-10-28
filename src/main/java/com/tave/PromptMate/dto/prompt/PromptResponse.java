package com.tave.PromptMate.dto.prompt;

public record PromptResponse(
        Long id,
        Long userId,
        Long categoryId,
        String title,
        String content,
        Boolean isPrivate
) {}
