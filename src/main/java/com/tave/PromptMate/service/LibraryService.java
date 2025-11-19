package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Evaluation;
import com.tave.PromptMate.domain.Library;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.library.CreateLibraryRequest;
import com.tave.PromptMate.dto.library.LibraryMapper;
import com.tave.PromptMate.dto.library.LibraryResponse;
import com.tave.PromptMate.repository.EvaluationRepository;
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
    private final EvaluationRepository evaluationRepository;

    // 라이브러리에 리라이팅 결과 저장하기
    public LibraryResponse save(CreateLibraryRequest req) {
        if (libraryRepository.existsByUser_IdAndRewriteResult_Id(req.userId(), req.rewriteResultId())) {
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

        Integer totalScore = getLatestScore(result.getId());

        return LibraryMapper.toResponse(saved, totalScore);
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> getMyLibraries(Long userId) {
        return libraryRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(library -> LibraryMapper.toResponse(
                        library,
                        getLatestScore(library.getRewriteResult().getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public LibraryResponse getOne(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));

        Integer totalScore = getLatestScore(lib.getRewriteResult().getId());
        return LibraryMapper.toResponse(lib, totalScore);
    }

    public void delete(Long id, Long userId) {
        Library lib = libraryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new NotFoundException("library not found: " + id));
        libraryRepository.delete(lib);
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> searchMyLibraries(Long userId, String keyword) {
        return libraryRepository.searchMyLibrariesNative(userId, keyword)
                .stream()
                .map(library -> LibraryMapper.toResponse(
                        library,
                        getLatestScore(library.getRewriteResult().getId())
                ))
                .toList();
    }

    //  공통 점수 가져오기
    private Integer getLatestScore(Long rewriteResultId) {
        return evaluationRepository
                .findTopByRewriteResultIdOrderByIdDesc(rewriteResultId)
                .map(Evaluation::getTotalScore)
                .orElse(null);
    }

}
