package com.tave.PromptMate.dto.prompt;

import com.tave.PromptMate.dto.evaluation.EvaluationResponse;

public record PromptAutoResponse(
        Long promptId,
        Long rewriteId,
        String rewrittenContent,
        EvaluationResponse evaluation
) {}
