package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Community;
import com.tave.PromptMate.domain.Community.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 내가 작성한 글 조회
    List<Community> findByUser_IdAndVisibilityNotOrderByCreatedAtDesc(
            Long userId,
            Visibility visibility
    );
}
