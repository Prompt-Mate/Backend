package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.service.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @PostMapping("/{communityId}/likes")
    public ResponseEntity<Void> like(
            @PathVariable Long communityId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        communityLikeService.like(communityId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{communityId}/likes")
    public ResponseEntity<Void> unlike(
            @PathVariable Long communityId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        communityLikeService.unlike(communityId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
