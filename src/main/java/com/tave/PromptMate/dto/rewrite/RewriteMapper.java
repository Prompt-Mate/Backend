package com.tave.PromptMate.dto.rewrite;

import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;

public final class RewriteMapper {
    private RewriteMapper(){}

    public static RewriteResult toEntity(CreateRewriteRequest req, Prompt prompt){
        return RewriteResult.builder()
                .prompt(prompt)
                .modelName(req.modelName())
                .content(req.content())
                .inputTokens(req.inputTokens())
                .outputTokens(req.outputTokens())
                .latencyMs(req.latencyMs())
                .build();
    }

    public static RewriteResponse toResponse(RewriteResult r) {
        return new RewriteResponse(
                r.getId(),
                r.getPrompt().getId(),
                r.getModelName(),
                r.getContent(),
                r.getInputTokens(),
                r.getOutputTokens(),
                r.getLatencyMs()
        );
    }
}
