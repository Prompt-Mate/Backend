package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommunityPostRequest (
    @NotBlank String title,
    @NotBlank String description,
    @NotNull
    Community.Visibility visibility,
    String imageUrl
) {}
