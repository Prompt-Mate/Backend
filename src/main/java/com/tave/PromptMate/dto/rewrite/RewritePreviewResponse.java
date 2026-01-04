package com.tave.PromptMate.dto.rewrite;

public record RewritePreviewResponse(
        String rewrittenPrompt,
        Long latencyMs,
        String modelName,
        String version
) {}
