package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.community.CommunityPostMapper;
import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.community.CreateCommunityPostRequest;
import com.tave.PromptMate.dto.community.UpdateCommunityPostRequest;
import com.tave.PromptMate.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    // 커뮤니티 글 작성
    @PostMapping("/posts")
    public ResponseEntity<CommunityPostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateCommunityPostRequest request
    ) {
        Long userId=principal.getUserId();

        CommunityPostResponse response = communityService.createPost(request, userId);

        URI location = URI.create("/api/community/posts/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    //커뮤니티 글 수정
    @PatchMapping("/posts/{postId}")
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
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ){
        Long userId=principal.getUserId();
        communityService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}