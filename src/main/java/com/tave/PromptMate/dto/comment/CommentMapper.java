package com.tave.PromptMate.dto.comment;

import com.tave.PromptMate.domain.Comment;

import java.util.Comparator;
import java.util.List;

public class CommentMapper {

    public static CommentResponse toResponse(Comment comment){
        List<CommentResponse> replies=comment.getReplies().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(CommentMapper::toResponse)
                .toList();

        return new CommentResponse(
                comment.getId(),
                comment.getCommunity().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getParent() !=null ? comment.getParent().getId() : null,
                comment.getContent(),
                comment.getStatus().name(),
                comment.getCreatedAt(),
                replies
        );
    }
}
