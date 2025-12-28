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
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid@RequestBody CreateCommentRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        Long userId=userDetails.getUserId();
        return  ResponseEntity.ok(commentService.createdComment(req,userId));
    }

    // 답글 등록
    @PostMapping("/replies")
    public ResponseEntity<CommentResponse> createReply(
            @Valid @RequestBody CreateReplyRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId=userDetails.getUserId();
        return ResponseEntity.ok(commentService.createReply(req, userId));
    }

    // 게시글 기준 댓글/답글 조회
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<CommentResponse>> getCommentByCommunity(@PathVariable Long communityId){
        return ResponseEntity.ok(commentService.getCommentsByCommunity(communityId));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        Long userId=userDetails.getUserId();
        return ResponseEntity.ok(commentService.updateComment(commentId,req,userId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId=userDetails.getUserId();
        commentService.deleteComment(commentId,userId);
        return ResponseEntity.noContent().build();
    }
}
