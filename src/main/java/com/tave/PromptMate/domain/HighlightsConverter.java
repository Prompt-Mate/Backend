package com.tave.PromptMate.domain;

import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class HighlightsConverter extends JsonConverter<HighlightsPayload> {
    public HighlightsConverter() {
        super(HighlightsPayload.class);
    }
}