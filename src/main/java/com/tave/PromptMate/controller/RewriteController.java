package com.tave.PromptMate.controller;

import com.tave.PromptMate.common.RewriteRunner;
import com.tave.PromptMate.dto.rewrite.*;
import com.tave.PromptMate.service.RewriteResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RewriteController {

    private final RewriteResultService rewriteResultService;
    private final RewriteRunner rewriteRunner;


    @PostMapping("/rewrite")
    public ResponseEntity<RewritePreviewResponse> preview(@Valid @RequestBody RewritePreviewRequest req) {
        AiRewriteResult result = rewriteRunner.run(req.prompt());
        System.out.println("RewriteRunner bean = " + rewriteRunner.getClass().getName());


        return ResponseEntity.ok(new RewritePreviewResponse(
                result.rewrittenContent(),
                result.latencyMs(),
                result.modelName(),
                result.version()
        ));

    }


    // 리라이티 결과 생성(저장)하기
    @PostMapping("rewrite-results")
    public ResponseEntity<RewriteResponse> create(@RequestBody @Valid CreateRewriteRequest req){
        RewriteResponse res = rewriteResultService.create(req);
        return ResponseEntity.created(URI.create("/api/rewrite-results/" + res.id())).body(res);
    }

    // 프롬프트별 리라이팅 결과 전체 목록 조회
    @GetMapping("/prompts/{promptId}/rewrites")
    public ResponseEntity<List<RewriteResponse>> getByPrompt(@PathVariable Long promptId){
        return ResponseEntity.ok(rewriteResultService.getListByPrompt(promptId));
    }

    // 프롬프트별 최신 1건 조회
    @GetMapping("/prompts/{promptId}/rewrites/latest")
    public ResponseEntity<RewriteResponse> getLatest(@PathVariable Long promptId){
        RewriteResponse res = rewriteResultService.getLatestByPrompt(promptId);
        return ResponseEntity.ok(res);
    }

    // 리라이팅 결과 단건 조회
    @GetMapping("/rewrite-results/{id}")
    public ResponseEntity<RewriteResponse> getOne(@PathVariable Long id){
        return ResponseEntity.ok(rewriteResultService.getOne(id));
    }

    // 리라이팅 삭제하기
    @DeleteMapping("/rewrite-results/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        rewriteResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}