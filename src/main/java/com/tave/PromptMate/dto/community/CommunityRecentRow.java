package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;

import java.time.LocalDateTime;

public record CommunityRecentRow(
        Long postId,
        String title,
        Platform platform,
        PromptCategory category,
        LocalDateTime lastViewedAt
){}
