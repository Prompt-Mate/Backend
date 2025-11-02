package com.tave.PromptMate.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest (
    @NotNull @Size(max = 60) String name,
    @Size(max = 2000) String description
) {}
