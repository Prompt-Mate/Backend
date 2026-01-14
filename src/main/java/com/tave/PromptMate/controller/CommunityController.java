package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.common.S3Uploader;
import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;
import com.tave.PromptMate.dto.community.*;
import com.tave.PromptMate.service.CommunityRecentService;
import com.tave.PromptMate.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    private final S3Uploader s3Uploader;
    private final CommunityRecentService communityRecentService;

    // 커뮤니티 글 작성
    @PostMapping(value="/posts", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityPostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("rewriteResultId") Long rewriteResultId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("visibility") Community.Visibility visibility,
            @RequestPart(value="image" ,required = false) MultipartFile image
            ) throws IOException {
        Long userId=principal.getUserId();

        // 이미지 업로드
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Uploader.uploadImage(image, "community");
        }

        CreateCommunityPostRequest request = new CreateCommunityPostRequest(
                rewriteResultId, title, description, visibility,imageUrl
        );

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

    // 커뮤니티 글 목록 조회(최신순/조회순/좋아요순), 검색 포함
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPostResponse>> getPosts(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Platform platform,
            @RequestParam(required = false) PromptCategory category,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = (principal == null) ? null : principal.getUserId();

        String p = (platform == null) ? null : platform.name();
        String c = (category == null) ? null : category.name();

        return ResponseEntity.ok(communityService.getPosts(q, p, c, sort, userId));
    }

    // 커뮤니티 글 단건 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityPostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = (principal == null) ? null : principal.getUserId();
        return ResponseEntity.ok(communityService.getPost(postId, userId));
    }
    // 최근 본 프롬프트
    @GetMapping("/recent")
    public ResponseEntity<List<CommunityRecentRow>> getRecentCommunities(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "20") int limit
    ){
        Long userId= principal.getUserId();
        return ResponseEntity.ok(communityRecentService.getRecent(userId,limit));
    }
}