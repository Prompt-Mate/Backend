package com.tave.PromptMate.dto.rewrite;

import java.time.LocalDateTime;

public record RewriteHistoryItemResponse(
        Long rewriteResultId,
        Long promptId,
        String beforePrompt,      // 원문
        String rewrittenPrompt,   // 결과
        Long latencyMs,
        String modelName,
        String version,
        LocalDateTime createdAt
) {}
