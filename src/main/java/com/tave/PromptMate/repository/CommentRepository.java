package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCommunityIdAndParentIsNullOrderByCreatedAtAsc(Long communityId);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByCommunityId(long communityId);
}
