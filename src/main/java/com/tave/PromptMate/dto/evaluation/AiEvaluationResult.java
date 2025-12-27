package com.tave.PromptMate.dto.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiEvaluationResult (
        @JsonProperty("overall_score") Integer overallScore,
        @JsonProperty("clarity_score") Integer clarityScore,
        @JsonProperty("specificity_score") Integer specificityScore,
        @JsonProperty("structure_score") Integer structureScore,
        @JsonProperty("language_score") Integer languageScore,
        @JsonProperty("consistency_score") Integer consistencyScore,

        @JsonProperty("clarity_comment") String clarityComment,
        @JsonProperty("specificity_comment") String specificityComment,
        @JsonProperty("structure_comment") String structureComment,
        @JsonProperty("language_comment") String languageComment,
        @JsonProperty("consistency_comment") String consistencyComment,

        @JsonProperty("summary_feedback") String summaryFeedback,
        @JsonProperty("model_name") String modelName,
        @JsonProperty("version") String version
){}
