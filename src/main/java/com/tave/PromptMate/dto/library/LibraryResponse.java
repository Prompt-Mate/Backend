package com.tave.PromptMate.dto.library;

import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;

import java.time.LocalDateTime;

public record LibraryResponse(
        Long id,
        Long userId,
        Long rewriteResultId,
        String savedTitle,
        String content,
        Platform platform,
        PromptCategory category,
        String imageUrl,
        LocalDateTime createdAt
) {}
