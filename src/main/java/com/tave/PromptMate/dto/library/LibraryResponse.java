package com.tave.PromptMate.dto.library;

import java.time.LocalDateTime;

public record LibraryResponse(
        Long id,
        Long userId,
        Long promptId,
        Long rewriteResultId,
        String savedTitle,
        String content,
        LocalDateTime createdAt
) {}
