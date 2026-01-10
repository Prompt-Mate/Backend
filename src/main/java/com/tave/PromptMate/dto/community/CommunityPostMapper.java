package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.service.CommunityLikeService;

public class CommunityPostMapper {

    public static CommunityPostResponse toResponse(Community community, long likeCount, long commentCount, boolean isLiked) {

        RewriteResult rewriteResult=community.getRewriteResult();

        return new CommunityPostResponse(
                community.getId(),
                rewriteResult.getId(),
                community.getUser().getId(),
                community.getUser().getNickname(),
                community.getCategory().getId(),
                community.getCategory().getName(),
                community.getTitle(),
                rewriteResult.getContent(),
                community.getVisibility(),
                community.getCreatedAt(),
                community.getViewCount(),
                likeCount,
                commentCount,
                isLiked

        );
    }
}
