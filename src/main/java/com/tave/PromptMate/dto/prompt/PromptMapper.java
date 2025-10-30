package com.tave.PromptMate.dto.prompt;

import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.User;

public class PromptMapper {
    private PromptMapper(){}

    public static Prompt toEntity(CreatePromptRequest req, User user, Category category) {
        return Prompt.builder()
                .user(user)
                .category(category)
                .title(req.title())
                .content(req.content())
                .isPrivate(req.isPrivate())
                .build();
    }

    public static PromptResponse toResponse(Prompt p) {
        return new PromptResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getTitle(),
                p.getContent(),
                p.getIsPrivate()
        );
    }
}
