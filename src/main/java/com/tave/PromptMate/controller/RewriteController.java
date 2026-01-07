package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.common.RewriteRunner;
import com.tave.PromptMate.dto.rewrite.*;
import com.tave.PromptMate.service.RewriteResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RewriteController {

    private final RewriteResultService rewriteResultService;
    private final RewriteRunner rewriteRunner;

    @GetMapping("/me")
    public ResponseEntity<String> getAuthenticatedUserInfo(
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요한 기능입니다.");
        }

        Long userId = principal.getUserId();
        return ResponseEntity.ok("인증된 사용자 ID: " + userId);
    }

    private Long requireUserId(CustomUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요한 기능입니다.");
        }
        return principal.getUserId();
    }



    @PostMapping("/rewrite")
    public ResponseEntity<RewritePreviewResponse> preview(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody RewritePreviewRequest req) {

        Long userId = requireUserId(principal);

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
    public ResponseEntity<RewriteResponse> create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid CreateRewriteRequest req){

        Long userId = requireUserId(principal);

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
    public ResponseEntity<RewriteResponse> getLatest(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long promptId){

        Long userId = requireUserId(principal);

        RewriteResponse res = rewriteResultService.getLatestByPrompt(promptId);
        return ResponseEntity.ok(res);
    }

    // 리라이팅 결과 단건 조회
    @GetMapping("/rewrite-results/{id}")
    public ResponseEntity<RewriteResponse> getOne(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id){

        Long userId = requireUserId(principal);

        return ResponseEntity.ok(rewriteResultService.getOne(id));
    }

    // 리라이팅 삭제하기
    @DeleteMapping("/rewrite-results/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id){

        Long userId = requireUserId(principal);

        rewriteResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}