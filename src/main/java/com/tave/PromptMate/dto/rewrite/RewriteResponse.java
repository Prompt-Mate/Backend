package com.tave.PromptMate.dto.rewrite;

import java.time.LocalDateTime;

public record RewriteResponse(
        Long id,
        Long promptId,
        String modelName,
        String content,
        Integer inputTokens,
        Integer outputTokens,
        Long latencyMs,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
