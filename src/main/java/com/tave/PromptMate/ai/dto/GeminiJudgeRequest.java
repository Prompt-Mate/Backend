package com.tave.PromptMate.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeminiJudgeRequest {

    @Schema(description = "평가할 프롬프트", example = "맛있는 파스타 레시피 알려줘")
    private String prompt;

}
