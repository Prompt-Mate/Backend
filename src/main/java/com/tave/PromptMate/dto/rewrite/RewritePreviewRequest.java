package com.tave.PromptMate.dto.rewrite;

import jakarta.validation.constraints.NotBlank;

public record RewritePreviewRequest(
        @NotBlank(message = "prompt is required")
        String prompt
) {}
