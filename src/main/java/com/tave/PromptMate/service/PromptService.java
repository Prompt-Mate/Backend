package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.prompt.CreatePromptRequest;
import com.tave.PromptMate.dto.prompt.PromptMapper;
import com.tave.PromptMate.dto.prompt.PromptResponse;
import com.tave.PromptMate.repository.CategoryRepository;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public List<PromptResponse> getAll(){
        return promptRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PromptMapper::toResponse)
                .toList();
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

        if (!prompt.getUser().getId().equals(requestUserId)) {
            throw new IllegalStateException("no permission to delete this prompt");
        }
        promptRepository.delete(prompt);
    }
}
