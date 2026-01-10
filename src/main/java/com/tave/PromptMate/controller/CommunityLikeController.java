package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.community.CommunityLikeToggleResponse;
import com.tave.PromptMate.service.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @PostMapping("/{postId}/likes")
    public ResponseEntity<CommunityLikeToggleResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommunityLikeToggleResponse response =
                communityLikeService.toggleLike(postId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
