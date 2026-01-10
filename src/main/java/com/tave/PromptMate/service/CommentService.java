package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Comment;
import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.comment.*;
import com.tave.PromptMate.repository.CommentRepository;
import com.tave.PromptMate.repository.CommunityRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    // 댓글 등록
    public CommentResponse createdComment(CreateCommentRequest req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        Community community = communityRepository.findById(req.communityId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다. id=" + req.communityId()));

        Comment comment = Comment.create(community, user, null, req.content());
        Comment saved = commentRepository.save(comment);

        return CommentMapper.toResponse(saved);
    }

    // 답글 등록 (부모 댓글 기준)
    public CommentResponse createReply(CreateReplyRequest req, Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("존재하지 않는 사용자입니다. id="+userId));

        Comment parent=commentRepository.findById(req.parentId())
                .orElseThrow(()->new NotFoundException("존재하지 않는 부모 댓글입니다. id="+req.parentId()));

        if(parent.isRemoved()){
            throw new IllegalStateException("삭제된 댓글에는 답글을 달 수 없습니다.");
        }

        Community community=parent.getCommunity();

        //답글 생성
        Comment reply=Comment.create(community, user, parent, req.content());

        Comment saved=commentRepository.save(reply);
        return CommentMapper.toResponse(saved);
    }

    // 게시글 기준 댓글/답글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByCommunity(Long communityId){

        communityRepository.findById(communityId)
                .orElseThrow(()->new NotFoundException("존재하지 않는 게시글입니다. id="+communityId));

        List<Comment> roots=commentRepository.findByCommunityIdAndParentIsNullOrderByCreatedAtAsc(communityId);

        return roots.stream()
                .map(CommentMapper::toResponse)
                .toList();
    }

    // 댓글 수정 (작성자만 가능)
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest req, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다. id=" + commentId));

        if (comment.isRemoved()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        comment.updateContent(req.content());
        return CommentMapper.toResponse(comment);
    }
        // 댓글 삭제 (작성자만 가능)
        public void deleteComment(Long commentId, Long userId){
            Comment comment=commentRepository.findById(commentId)
                    .orElseThrow(()->new NotFoundException("존재하지 않는 댓글입니다. id="+commentId));

            if(!comment.getUser().getId().equals(userId)){
                throw new IllegalStateException("삭제 권한이 없습니다.");
            }

            comment.remove();
        }


}
