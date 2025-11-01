package com.tave.PromptMate.controller;

import com.tave.PromptMate.dto.prompt.CreatePromptRequest;
import com.tave.PromptMate.dto.prompt.PromptResponse;
import com.tave.PromptMate.service.PromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

    // 전체 프롬프트 목록 조회
    @GetMapping
    public ResponseEntity<List<PromptResponse>> getAllPrompts(){
        return ResponseEntity.ok(promptService.getAll());
    }

    // 프롬프트 단건 조회
    @GetMapping("/{promptId}")
    public ResponseEntity<PromptResponse> getPrompt(@PathVariable Long promptId){
        PromptResponse res = promptService.getById(promptId);
        return ResponseEntity.ok(res);
    }

    // 특정 유저의 프롬프트 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PromptResponse>> getPromptsByUser(@PathVariable Long userId){
        List<PromptResponse> res = promptService.getByPrompts(userId);
        return ResponseEntity.ok(res);
    }

    // 카테고리별 목록 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PromptResponse>> getPromptsByCategory(@PathVariable Long categoryId){
        List<PromptResponse> res = promptService.getByCategory(categoryId);
        return ResponseEntity.ok(res);
    }

    // 프롬프트 삭제
    @DeleteMapping("/{promptId}")
    public ResponseEntity<Void> deletePrompt(@PathVariable Long promptId, @RequestParam Long userId){
        promptService.delete(promptId, userId);
        return ResponseEntity.noContent().build();
    }
}
