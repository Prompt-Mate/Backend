package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community.Visibility;
import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;

import java.time.LocalDateTime;

public record CommunityPostRow(
        Long id,
        Long rewriteResultId,
        Long userId,
        String nickname,
        String title,
        String promptContent,
        Visibility visibility,
        String description,
        LocalDateTime createdAt,
        long viewCount,
        long likeCount,
        long commentCount,
        boolean isLiked,
        Platform platform,
        PromptCategory category,
        String imageUrl
) {}
