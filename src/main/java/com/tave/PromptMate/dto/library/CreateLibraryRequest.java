package com.tave.PromptMate.dto.library;

import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;
import jakarta.validation.constraints.NotNull;

public record CreateLibraryRequest(
        @NotNull Long rewriteResultId,
        String savedTitle,
        Platform platform,
        PromptCategory category,
        String imageUrl
) {}