package com.tave.PromptMate.dto.rewrite;

public record RewriteResponse(
        Long id,
        Long promptId,
        String modelName,
        String content,
        Integer inputTokens,
        Integer outputTokens,
        Long latencyMs
) {
}
