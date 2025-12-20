package com.tave.PromptMate.dto.community;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Prompt;

public class CommunityPostMapper {

    public static CommunityPostResponse toResponse(Community community) {
        Prompt prompt = community.getPrompt();

        return new CommunityPostResponse(
                community.getId(),
                prompt.getId(),
                community.getUser().getId(),
                community.getUser().getNickname(),
                community.getCategory().getId(),
                community.getCategory().getName(),
                prompt.getTitle(),
                prompt.getContent(),
                community.getVisibility(),
                community.getCreatedAt()
        );
    }
}
