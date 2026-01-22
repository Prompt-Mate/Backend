package com.tave.PromptMate.ai.repository;

import com.tave.PromptMate.ai.domain.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JudgeRepository extends JpaRepository<Judge,Long> {
    Optional<Judge> findByRewriteResultId(Long rewriteResultId);
}
