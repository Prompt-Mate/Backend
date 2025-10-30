package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
