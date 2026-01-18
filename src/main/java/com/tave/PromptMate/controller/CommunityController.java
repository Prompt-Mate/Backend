package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;
import com.tave.PromptMate.dto.community.*;
import com.tave.PromptMate.service.CommunityRecentService;
import com.tave.PromptMate.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name="커뮤니티 게시글 API")
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityRecentService communityRecentService;

    // 커뮤니티 글 작성
    @PostMapping("/posts")
    @Operation(summary = "게시글 작성", description = "게시글 작성 입니다. 이미지는 라이브러리 저장 시 첨부됩니다.")
    public ResponseEntity<CommunityPostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateCommunityPostRequest request
    ) {
        Long userId = principal.getUserId();

        CommunityPostResponse response = communityService.createPost(request, userId);

        URI location = URI.create("/api/community/posts/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    //커뮤니티 글 수정
    @PatchMapping("/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "해당 게시글을 수정합니다.")
    public ResponseEntity<CommunityPostResponse> updatePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody UpdateCommunityPostRequest request
    ){
        Long userId=principal.getUserId();
        CommunityPostResponse response=communityService.updatePost(postId, request, userId);
        return ResponseEntity.ok(response);
    }

    //커뮤니티 글 삭제
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "해당 게시글을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ){
        Long userId=principal.getUserId();
        communityService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    // 커뮤니티 글 목록 조회(최신순/조회순/좋아요순), 검색 포함
    @GetMapping("/posts")
    @Operation(summary = "게시글 조회", description = "해당 게시글을 (최신순/조회순/좋아요) 순으로 조회합니다. 검색 포함")
    public ResponseEntity<List<CommunityPostResponse>> getPosts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Platform platform,
            @RequestParam(required = false) PromptCategory category,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = (principal == null) ? null : principal.getUserId();

        String p = (platform == null) ? null : platform.name();
        String c = (category == null) ? null : category.name();

        return ResponseEntity.ok(communityService.getPosts(search, p, c, sort, userId));
    }

    // 커뮤니티 글 단건 조회
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 상세조회", description = "해당 게시글의 내용,댓글 등을 조회합니다.")
    public ResponseEntity<CommunityPostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = (principal == null) ? null : principal.getUserId();
        return ResponseEntity.ok(communityService.getPost(postId, userId));
    }

    // 최근 본 프롬프트
    @GetMapping("/recent")
    @Operation(
            summary = "최근 본 프롬프트 조회",
            description = "사용자가 최근에 조회한 프롬프트 목록을 최신순으로 조회합니다."
    )
    public ResponseEntity<List<CommunityRecentRow>> getRecentCommunities(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "20") int limit
    ){
        Long userId= principal.getUserId();
        return ResponseEntity.ok(communityRecentService.getRecent(userId,limit));
    }

    // 오늘의 인기 프롬프트
    @GetMapping("/popular")
    public ResponseEntity<List<CommunityPostResponse>> popular(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = (principal == null) ? null : principal.getUserId();
        return ResponseEntity.ok(communityService.getPopularTop5(userId));
    }

}