package com.tave.PromptMate.dto.library;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibrarySaveRequest {
    private Long promptId;
    private String savedTitle;
}
