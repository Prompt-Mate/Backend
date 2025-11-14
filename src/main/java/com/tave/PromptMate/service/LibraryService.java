package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Library;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryMapper;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.repository.LibraryRepository;
import com.tave.PromptMate.repository.RewriteResultRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final RewriteResultRepository rewriteResultRepository;
    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;

    // 라이브러리에 리라이팅 결과 저장하기
    public LibraryResponse save(CreateLibraryRequest req) {
        if (libraryRepository.existsByUser_IdAndRewriteResult_Id(req.userId(), req.rewriteResultId())){
            throw new IllegalStateException("이미 라이브러리에 저장된 결과입니다.");
        }
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new NotFoundException("user not found: " + req.userId()));

        RewriteResult result = rewriteResultRepository.findById(req.rewriteResultId())
                .orElseThrow(() -> new NotFoundException("rewrite result not found: " + req.rewriteResultId()));

        Library library = Library.builder()
                .user(user)
                .prompt(result.getPrompt())
                .rewriteResult(result)
                .savedTitle(req.savedTitle())
                .build();

        Library saved = libraryRepository.save(library);
        return LibraryMapper.toResponse(saved);
    }

    // 내 라이브러리 목록 조회하기
    @Transactional(readOnly = true)
    public List<LibraryResponse> getMyLibraries(Long userId){
        return libraryRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LibraryMapper::toResponse)
                .toList();
    }

    // 단건 조회
    @Transactional
    public LibraryResponse getOne(Long id, Long userId){
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        return LibraryMapper.toResponse(lib);
    }

    // 삭제하기
    public void delete(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        libraryRepository.delete(lib);
    }

    //라이브러리 검색
    public List<LibraryResponse> searchMyLibraries(Long userId, String keyword){
        return libraryRepository
                .findByUser_IdAndSavedTitleContainingIgnoreCaseOrderByCreatedAtDesc(userId,keyword)
                .stream()
                .map(LibraryMapper::toResponse)
                .toList();
    }
}
