package com.tave.PromptMate.controller;

import com.tave.PromptMate.dto.evaluation.CreateEvaluationRequest;
import com.tave.PromptMate.dto.evaluation.EvaluationResponse;
import com.tave.PromptMate.dto.evaluation.EvaluationSummaryResponse;
import com.tave.PromptMate.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    // 평가 생성하기
    @PostMapping
    public ResponseEntity<EvaluationResponse> create(@Valid @RequestBody CreateEvaluationRequest req){
        EvaluationResponse res = evaluationService.createEvaluation(req);
        return ResponseEntity
                .created(URI.create("/api/evaluations/" + res.id()))
                .body(res);
    }

    // 평가 단건 조회
    @GetMapping("/{evaluationId}")
    public ResponseEntity<EvaluationResponse> getOne(@PathVariable Long evaluationId){
        return ResponseEntity.ok(evaluationService.getById(evaluationId));
    }

    // 프롬프트 평가 목록 - 페이징
    @GetMapping("/prompt/{promptId}")
    public ResponseEntity<Page<EvaluationResponse>> byPrompt (
            @PathVariable Long promptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByPromptPage(promptId, page, size));
    }

    // 리라이트 결과별 평가 목록
    @GetMapping("/rewrite/{rewriteId}")
    public ResponseEntity<Page<EvaluationResponse>> byRewrite(
            @PathVariable Long rewriteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(evaluationService.getEvaluationsByRewritePage(rewriteId, page, size));
    }

    // 프롬프트 요약 부분
    @GetMapping("/prompt/{promptId}/summary")
    public ResponseEntity<EvaluationSummaryResponse> summary(@PathVariable Long promptId) {
        return ResponseEntity.ok(evaluationService.getSummaryByPrompt(promptId));
    }

    // 평가 삭제
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<Void> delete(@PathVariable Long evaluationId) {
        evaluationService.deleteEvaluation(evaluationId);
        return ResponseEntity.noContent().build();
    }


}
