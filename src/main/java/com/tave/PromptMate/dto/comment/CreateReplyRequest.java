package com.tave.PromptMate.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReplyRequest(
        @NotNull long parentId,
        @NotBlank String content
) {}
