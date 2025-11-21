package com.tave.PromptMate.controller;

import com.tave.PromptMate.dto.community.CommunityPostResponse;
import com.tave.PromptMate.dto.community.CreateCommunityPostRequest;
import com.tave.PromptMate.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody CreateCommunityPostRequest request
    ) {
        CommunityPostResponse response = communityService.createPost(request);
        URI location = URI.create("/api/community/posts/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
}
