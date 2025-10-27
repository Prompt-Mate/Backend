package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
