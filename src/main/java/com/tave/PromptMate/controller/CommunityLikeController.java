package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.community.CommunityLikeToggleResponse;
import com.tave.PromptMate.service.CommunityLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
@Tag(name="커뮤니티 좋아요 API")
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요", description = "해당 게시글에 좋아요를 합니다.")
    public ResponseEntity<CommunityLikeToggleResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommunityLikeToggleResponse response =
                communityLikeService.toggleLike(postId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
