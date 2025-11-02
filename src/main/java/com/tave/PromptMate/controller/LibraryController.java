package com.tave.PromptMate.controller;

import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/libraries")
public class LibraryController {

    private final LibraryService libraryService;

    // 라이브러리에 리라이팅 결과 저장하기
    @PostMapping
    public ResponseEntity<LibraryResponse> save(@Valid @RequestBody CreateLibraryRequest req){
        LibraryResponse res = libraryService.save(req);
        return ResponseEntity.created(URI.create("/api/libraries/" + res.id()))
                .body(res);
    }

    // 내 라이브러리 목록 조회
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<LibraryResponse>> myList(@PathVariable Long userId){
        return ResponseEntity.ok(libraryService.getMyLibraries(userId));
    }

    // 단건 조회
    @GetMapping("/{id}/users/{userId}")
    public ResponseEntity<LibraryResponse> getOne(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(libraryService.getOne(id, userId));
    }

    // 삭제
    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long userId) {
        libraryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}

