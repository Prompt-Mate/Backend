package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;

public record CommunityPostResponse(
        Long id,
        Long rewriteResultId,
        Long userId,
        String nickname,
        String title,
        String promptContent,
        String description,
        Community.Visibility visibility,
        java.time.LocalDateTime createdAt,
        long viewCount,
        long likeCount,
        long commentCount,
        boolean isLiked,
        Platform platform,
        PromptCategory category,
        String imageUrl
) {}
