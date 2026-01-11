package com.tave.PromptMate.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String nickname,
        Long parentId,
        String content,
        String status,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {}
