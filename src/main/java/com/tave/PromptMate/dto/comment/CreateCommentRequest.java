package com.tave.PromptMate.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest (
    @NotNull Long communityId,
    @NotBlank String content
) {}