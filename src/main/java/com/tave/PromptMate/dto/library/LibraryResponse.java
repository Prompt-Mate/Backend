package com.tave.PromptMate.dto.library;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LibraryResponse {
    private Long promptId;
    private String savedTitle;
    private LocalDateTime createdAt;
}
