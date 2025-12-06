package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<LibraryResponse> save(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateLibraryRequest req){

        Long userId=principal.getUserId();

        LibraryResponse res = libraryService.save(req, userId);

        return ResponseEntity.created(URI.create("/api/libraries/" + res.id()))
                .body(res);
    }

    // 내 라이브러리 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<LibraryResponse>> myList(@AuthenticationPrincipal CustomUserDetails principal){

        Long userId=principal.getUserId();
        return ResponseEntity.ok(libraryService.getMyLibraries(userId));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<LibraryResponse> getOne(@PathVariable Long id,
                                                  @AuthenticationPrincipal CustomUserDetails principal) {

        Long userId= principal.getUserId();

        return ResponseEntity.ok(libraryService.getOne(id, userId));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,  @AuthenticationPrincipal CustomUserDetails principal) {

        Long userId= principal.getUserId();

        libraryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    //내가 작성한 게시글 조회
    @GetMapping("/my-posts")
    public ResponseEntity<List<CommunityPostResponse>>getMyPosts(
            @AuthenticationPrincipal CustomUserDetails principal
            ){
        Long userId=principal.getUserId();

        List<CommunityPostResponse> responses=libraryService.getMyPosts(userId);

        return ResponseEntity.ok(responses);
    }
}
