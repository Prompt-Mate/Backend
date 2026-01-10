package com.tave.PromptMate.dto.rewrite;

public record RewritePreviewResponse(
        Long rewriteResultId,
        String rewrittenPrompt,
        Long latencyMs,
        String modelName,
        String version
) {}
