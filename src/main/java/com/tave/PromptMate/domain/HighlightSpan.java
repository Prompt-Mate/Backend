package com.tave.PromptMate.domain;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HighlightSpan {
    private String type;  // good, feedback 등
    private int start;
    private int end;
    private String note;
}