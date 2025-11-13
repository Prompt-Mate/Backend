package com.tave.PromptMate.dto.prompt;

import jakarta.validation.constraints.Size;

public record UpdatePromptRequest (
    Long categoryId,
    @Size(max = 100) String title,
    String content
) {}
