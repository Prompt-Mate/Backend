package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommunityPostRequest(

        @NotNull
        Long categoryId,

        @NotNull
        Long rewriteResultId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        Visibility visibility
) {}
