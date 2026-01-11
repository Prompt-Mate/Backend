package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.domain.Comment;
import com.tave.PromptMate.dto.comment.CommentResponse;
import com.tave.PromptMate.dto.comment.CreateCommentRequest;
import com.tave.PromptMate.dto.comment.CreateReplyRequest;
import com.tave.PromptMate.dto.comment.UpdateCommentRequest;
import com.tave.PromptMate.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts/{postId}")
public class CommentController {

    private final CommentService commentService;

    // 댓글/답글 등록
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> createCommentOrReply(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(commentService.create(postId, req, userId));
    }

    // 게시글 기준 댓글/답글 조회
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId){
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(commentService.updateComment(commentId, req, userId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
