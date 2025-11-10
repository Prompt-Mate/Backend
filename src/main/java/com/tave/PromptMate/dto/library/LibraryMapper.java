package com.tave.PromptMate.dto.library;

import com.tave.PromptMate.domain.Library;

public class LibraryMapper {

    public static LibraryResponse toResponse(Library e) {
        return new LibraryResponse(
                e.getId(),
                e.getUser().getId(),
                e.getPrompt().getId(),
                e.getRewriteResult().getId(),
                e.getSavedTitle(),
                e.getRewriteResult().getContent(),
                e.getCreatedAt()
        );
    }
}
