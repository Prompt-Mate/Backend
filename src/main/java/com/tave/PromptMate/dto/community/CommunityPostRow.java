package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community.Visibility;

import java.time.LocalDateTime;

public record CommunityPostRow(
        Long id,
        Long rewriteResultId,
        Long userId,
        String nickname,
        Long categoryId,
        String categoryName,
        String title,
        String promptContent,
        Visibility visibility,
        LocalDateTime createdAt,
        long viewCount,
        long likeCount,
        long commentCount,
        boolean isLiked
) {}
