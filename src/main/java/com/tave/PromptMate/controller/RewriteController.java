package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.common.RewriteRunner;
import com.tave.PromptMate.dto.rewrite.*;
import com.tave.PromptMate.service.RewriteResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="리라이팅 API")
public class RewriteController {

    private final RewriteResultService rewriteResultService;
    private final RewriteRunner rewriteRunner;

    @Operation(hidden = true, deprecated = true)
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
    @Operation(summary = "리라이팅 출력", description = "리라이팅된 결과를 출력합니다.")
    public ResponseEntity<RewritePreviewResponse> preview(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody RewritePreviewRequest req) {

        Long userId = requireUserId(principal);

        AiRewriteResult result = rewriteRunner.run(req.prompt());
        System.out.println("RewriteRunner bean = " + rewriteRunner.getClass().getName());

        Long rewriteResultId = rewriteResultService.createDraft(userId, req.prompt(), result);

        return ResponseEntity.ok(new RewritePreviewResponse(
                rewriteResultId,
                result.rewrittenContent(),
                result.latencyMs(),
                result.modelName(),
                result.version()
        ));

    }

    //  내 리라이팅 히스토리(페이징)
    @Operation(hidden = true, deprecated = true)
    @GetMapping("/rewrite-results/my")
    public ResponseEntity<Page<RewriteHistoryItemResponse>> getMyHistory(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = requireUserId(principal);
        return ResponseEntity.ok(rewriteResultService.getMyHistory(userId, page, size));
    }

    //  내 최신 리라이팅 1건
    @Operation(hidden = true, deprecated = true)
    @GetMapping("/rewrite-results/my/latest")
    public ResponseEntity<RewriteHistoryItemResponse> getMyLatest(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = requireUserId(principal);
        return ResponseEntity.ok(rewriteResultService.getMyLatest(userId));
    }


    // 프롬프트별 리라이팅 결과 전체 목록 조회
    @Operation(hidden = true, deprecated = true)
    @GetMapping("/prompts/{promptId}/rewrites")
    public ResponseEntity<List<RewriteResponse>> getByPrompt(@PathVariable Long promptId){
        return ResponseEntity.ok(rewriteResultService.getListByPrompt(promptId));
    }

    // 프롬프트별 최신 1건 조회
    @Operation(hidden = true, deprecated = true)
    @GetMapping("/prompts/{promptId}/rewrites/latest")
    public ResponseEntity<RewriteResponse> getLatest(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long promptId){

        Long userId = requireUserId(principal);

        RewriteResponse res = rewriteResultService.getLatestByPrompt(promptId);
        return ResponseEntity.ok(res);
    }

    // 리라이팅 결과 단건 조회
    @Operation(hidden = true, deprecated = true)
    @GetMapping("/rewrite-results/{id}")
    public ResponseEntity<RewriteResponse> getOne(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id){

        Long userId = requireUserId(principal);

        return ResponseEntity.ok(rewriteResultService.getOne(id));
    }

    // 리라이팅 삭제하기
    @Operation(hidden = true, deprecated = true)
    @DeleteMapping("/rewrite-results/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id){

        Long userId = requireUserId(principal);

        rewriteResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}