package com.tave.PromptMate.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightsPayload {
    private List<HighlightSpan> before;
    private List<HighlightSpan> after;
}