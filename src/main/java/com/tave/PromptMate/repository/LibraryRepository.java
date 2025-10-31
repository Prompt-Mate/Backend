package com.tave.PromptMate.repository;

import com.tave.PromptMate.domain.library.LibraryId;
import com.tave.PromptMate.domain.library.LibraryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<LibraryId, LibraryItem> {
    boolean existsById(LibraryId id);
    Page<LibraryItem> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable );
    void deletedByUser_IdAndPrompt_Id(Long userId, Long promptId);
}
