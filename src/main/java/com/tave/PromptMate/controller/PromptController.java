package com.tave.PromptMate.controller;

import com.tave.PromptMate.dto.prompt.CreatePromptRequest;
import com.tave.PromptMate.dto.prompt.PromptResponse;
import com.tave.PromptMate.dto.prompt.UpdatePromptRequest;
import com.tave.PromptMate.service.PromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prompts")
public class PromptController {

    private final PromptService promptService;

    // 프롬프트 작성
    @PostMapping
    public ResponseEntity<PromptResponse> create(@Valid @RequestBody CreatePromptRequest req){
        PromptResponse res = promptService.create(req);

        return ResponseEntity
                .created(URI.create("/api/prompts/" + res.id()))
                .body(res);
    }

    // 전체 프롬프트 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<PromptResponse>> getAllPrompts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(promptService.getPage(page, size));
    }

    // 프롬프트 단건 조회
    @GetMapping("/{promptId}")
    public ResponseEntity<PromptResponse> getPrompt(@PathVariable Long promptId){
        PromptResponse res = promptService.getById(promptId);
        return ResponseEntity.ok(res);
    }


    // 특정 유저의 프롬프트 목록 조회 - 페이징
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<PromptResponse>> getPromptsByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(promptService.getByUserPage(userId, page, size));
    }

    // 카테고리별 목록 조회 - 페이징
    @GetMapping("/category/{categoryId}/page")
    public ResponseEntity<Page<PromptResponse>> getPromptsByCategoryPaged(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(promptService.getByCategoryPage(categoryId, page, size));
    }

    // 프롬프트 삭제
    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deletePrompt(@PathVariable Long promptId, @RequestParam Long userId){
        promptService.delete(promptId, userId);
        return ResponseEntity.noContent().build();
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<Page<PromptResponse>> search (
            @RequestParam(name = "q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(promptService.search(keyword, page, size));
    }

    // 프롬프트 수정
    @PatchMapping("/{promptId}")
    public ResponseEntity<PromptResponse> updatePrompt(
            @PathVariable Long promptId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdatePromptRequest req
    ){
        return ResponseEntity.ok(promptService.update(promptId, userId, req));
    }
}
