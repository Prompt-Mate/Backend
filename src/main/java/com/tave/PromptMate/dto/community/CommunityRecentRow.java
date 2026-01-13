package com.tave.PromptMate.dto.community;

import java.time.LocalDateTime;

public record CommunityRecentRow(
        Long postId,
        String title,
        String platform,
        String category,
        LocalDateTime lastViewedAt
){}
