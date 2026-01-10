package com.tave.PromptMate.dto.community;

public record CommunityLikeToggleResponse (
    boolean isLiked,
    long likeCount
){}
