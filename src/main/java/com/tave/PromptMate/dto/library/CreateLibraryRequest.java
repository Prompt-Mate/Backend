package com.tave.PromptMate.dto.library;

import jakarta.validation.constraints.NotNull;

public record CreateLibraryRequest(
        @NotNull Long rewriteResultId,
        String savedTitle
) {}