package com.tave.PromptMate.dto.evaluation;

import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.domain.Prompt;

public class EvaluationMapper {


    public static Evaluation toEntity(CreateEvaluationRequest req, Prompt prompt, RewriteResult rewrite) {
        return Evaluation.builder()
                .prompt(prompt)
                .rewriteResult(rewrite)

                .overallScore(req.overallScore())
                .clarityScore(req.clarityScore())
                .specificityScore(req.specificityScore())
                .structureScore(req.structureScore())
                .languageScore(req.languageScore())
                .consistencyScore(req.consistencyScore())

                .clarityComment(req.clarityComment())
                .specificityComment(req.specificityComment())
                .structureComment(req.structureComment())
                .languageComment(req.languageComment())
                .consistencyComment(req.consistencyComment())

                .summary(req.summaryFeedback())

                .build();
    }

    public static EvaluationResponse toResponse(Evaluation eval) {
        return new EvaluationResponse(
                eval.getId(),
                eval.getPrompt().getId(),
                eval.getRewriteResult().getId(),

                eval.getOverallScore(),
                eval.getClarityScore(),
                eval.getSpecificityScore(),
                eval.getStructureScore(),
                eval.getLanguageScore(),
                eval.getConsistencyScore(),

                eval.getClarityComment(),
                eval.getSpecificityComment(),
                eval.getStructureComment(),
                eval.getLanguageComment(),
                eval.getConsistencyComment(),

                eval.getSummary()
        );
    }

}
