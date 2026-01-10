package com.tave.PromptMate.ai.controller;

import com.tave.PromptMate.ai.dto.GeminiJudgeRequest;
import com.tave.PromptMate.ai.dto.GeminiJudgeResponse;
import com.tave.PromptMate.ai.service.GeminiJudgeService;
import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프롬프트 평가/피드백 API")
@RequestMapping("/api/judge")
public class GeminiJudgeController {

    private final GeminiJudgeService geminiJudgeService;

    @PostMapping
    public ResponseEntity<GeminiJudgeResponse> judge(@AuthenticationPrincipal CustomUserDetails principal,
                                                     @RequestBody GeminiJudgeRequest request){

        Long userId= principal.getUserId();
        GeminiJudgeResponse geminiJudgeResponse=geminiJudgeService.judgePrompt(userId,request.getPrompt());

        return ResponseEntity.ok(geminiJudgeResponse);


    }
}
