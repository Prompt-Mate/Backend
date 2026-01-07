package com.tave.PromptMate.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeminiJudgeResponse {
    private Integer overall_score;
    private Integer clarity_score;
    private Integer specificity_score;
    private Integer structure_score;
    private Integer language_score;
    private Integer consistency_score;
    private String clarity_comment;
    private String specificity_comment;
    private String structure_comment;
    private String language_comment;
    private String consistency_comment;
    private String summary_feedback;
}
