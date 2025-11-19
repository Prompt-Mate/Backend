package com.tave.PromptMate.dto.library;

import com.tave.PromptMate.domain.Library;

public class LibraryMapper {

    public static LibraryResponse toResponse(Library e, Integer totalScore) {

        Long categoryId = null;
        String categoryName = null;

        if (e.getPrompt().getCategory() != null) {
            categoryId = e.getPrompt().getCategory().getId();
            categoryName = e.getPrompt().getCategory().getName();
        }

        return new LibraryResponse(
                e.getId(),
                e.getUser().getId(),
                e.getPrompt().getId(),
                categoryId,
                categoryName,
                e.getRewriteResult().getId(),
                e.getSavedTitle(),
                e.getRewriteResult().getContent(),
                e.getCreatedAt(),
                totalScore
        );
    }

    public static LibraryResponse toResponse(Library e){
        return toResponse(e, null);
    }
}
