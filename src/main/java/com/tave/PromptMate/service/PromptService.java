package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.prompt.CreatePromptRequest;
import com.tave.PromptMate.dto.prompt.PromptMapper;
import com.tave.PromptMate.dto.prompt.PromptResponse;
import com.tave.PromptMate.dto.prompt.UpdatePromptRequest;
import com.tave.PromptMate.repository.CategoryRepository;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // 프롬프트 작성
    public PromptResponse create(CreatePromptRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new NotFoundException("user not found: " + req.userId()));

        Category category = null;
        if (req.categoryId() != null) {
            category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new NotFoundException("category not found: " + req.categoryId()));
        }

        Prompt prompt = PromptMapper.toEntity(req, user, category);
        Prompt saved = promptRepository.save(prompt);
        return PromptMapper.toResponse(saved);
    }

    // 전체 프롬프트 목록 조회
    @Transactional(readOnly = true)
    public Page<PromptResponse> getPage(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return promptRepository.findByDeletedFalse(pageable).map(PromptMapper::toResponse);
    }

    // 프롬프트 단건 조회
    @Transactional(readOnly = true)
    public PromptResponse getById(Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));
        return PromptMapper.toResponse(prompt);
    }

    // 특정 유저의 프롬프트 목록 조회
    @Transactional(readOnly = true)
    public List<PromptResponse> getByPrompts(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found: " + userId));

        return promptRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PromptMapper::toResponse)
                .toList();
    }

    // 카테고리별 목록 조회
    @Transactional(readOnly = true)
    public List<PromptResponse> getByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category not found: " + categoryId));

        return promptRepository.findAllByCategoryOrderByCreatedAtDesc(category)
                .stream()
                .map(PromptMapper::toResponse)
                .toList();
    }

    // 프롬프트 삭제 - 글 작성자만 삭제
    public void delete(Long promptId, Long requestUserId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));

        // 내가 쓴 글이 아니면 삭제 불가
        if (!prompt.getUser().getId().equals(requestUserId)) {
            throw new IllegalStateException("no permission to delete this prompt");
        }
        prompt.softDelete();
    }

    // 프롬프트 수정
    public PromptResponse update(Long promptId, Long requestUserId, UpdatePromptRequest req){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));
        if (!prompt.getUser().getId().equals(requestUserId)){
            throw new IllegalStateException("no permission to edit");
        }

        // 카테고리 변경하기
        if (req.categoryId() != null){
            Category category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new NotFoundException("category not found: " + req.categoryId()));
            prompt.changeCategory(category);
        }

        // 제목 변경
        if (req.title() != null) {
            prompt.changeTitle(req.title());
        }

        // 내용 변경
        if (req.content() != null && !req.content().isBlank()) {
            prompt.changeContent(req.content());
        }
        return PromptMapper.toResponse(prompt);
    }

    // 키워드 검색
    @Transactional(readOnly = true)
    public Page<PromptResponse> search(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return promptRepository.searchByKeyword(keyword, pageable)
                .map(PromptMapper::toResponse);
    }

    // 유저별 페이징
    @Transactional(readOnly = true)
    public Page<PromptResponse> getByUserPage(Long userId, int page, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found: " + userId));
        Pageable pageable = PageRequest.of(page, size);
        return promptRepository.findByUserIdAndDeletedFalse(userId, pageable)
                .map(PromptMapper::toResponse);
    }

    // 카테고리별 페이징
    @Transactional(readOnly = true)
    public Page<PromptResponse> getByCategoryPage(Long categoryId, int page, int size) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category not found: " + categoryId));
        Pageable pageable = PageRequest.of(page, size);
        return promptRepository.findByCategoryIdAndDeletedFalse(categoryId, pageable)
                .map(PromptMapper::toResponse);
    }
}
