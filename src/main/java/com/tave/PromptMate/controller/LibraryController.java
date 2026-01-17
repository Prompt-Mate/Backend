package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/libraries")
@Tag(name="라이브러리 API")
public class LibraryController {

    private final LibraryService libraryService;

    // 라이브러리에 리라이팅 결과 저장하기
    @PostMapping
    @Operation(summary = "리라이팅 결과 저장", description = "리라이팅된 결과를 라이브러리에 저장합니다.")
    public ResponseEntity<LibraryResponse> save(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateLibraryRequest req){
       Long userId= principal.getUserId();
       LibraryResponse res=libraryService.save(userId,req);

        return ResponseEntity.created(URI.create("/api/libraries/" + res.id()))
                .body(res);
    }

    // 내 라이브러리 목록 조회
    @GetMapping("/my")
    @Operation(summary = "라이브러리 목록 조회", description = "라이브러리 목록을 조회합니다.")
    public ResponseEntity<Page<LibraryResponse>> myList(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ){
        Long userId = principal.getUserId();
        return ResponseEntity.ok(libraryService.getMyLibraries(userId, page, size));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<LibraryResponse> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long userId = principal.getUserId();
        return ResponseEntity.ok(libraryService.getOne(id, userId));
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "저장된 프롬프트 삭제", description = "저장한 리라이팅된 프롬프트를 삭제합니다.")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long userId = principal.getUserId();
        libraryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    //내가 작성한 게시글 삭제
    @DeleteMapping("/my-posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "해당 게시글을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails principal){

        Long userId = principal.getUserId();
        libraryService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    //내가 작성한 게시글 조회
    @GetMapping("/my-posts")
    @Operation(summary = "작성한 게시글 조회", description = "내가 작성한 게시글들을 조회합니다.")
    public ResponseEntity<List<CommunityPostResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails principal){

        Long userId = principal.getUserId();
        return ResponseEntity.ok(libraryService.getMyPosts(userId));
    }

    //좋아요한 프롬프트 조회
    @GetMapping("/liked")
    @Operation(summary = "좋아요한 프롬프트 조회", description = "좋아요 누른 프롬프트들(게시글들)을 조회합니다.")
    public ResponseEntity<Page<CommunityPostResponse>> likedList(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size){

        Long userId = principal.getUserId();
        return ResponseEntity.ok(libraryService.getLikedPosts(userId, page, size));
    }

    //검색(태그, 키워드 기반)
    @GetMapping("/search")
    @Operation(summary = "라이브러리 검색", description = "태그, 키워드 기반으로 프롬프트들을 검색합니다.")
    public ResponseEntity<Page<LibraryResponse>> search(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size){

        Long userId = principal.getUserId();
        return ResponseEntity.ok(
                libraryService.searchMyLibraries(userId, keyword, platform, category, page, size)
        );
    }
}

