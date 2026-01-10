package com.tave.PromptMate.dto.rewrite;

public record AiRewriteResult(
        String rewrittenContent,
        Integer inputTokens,
        Integer outputTokens,
        Long latencyMs,
        String modelName,
        String version
) {}

