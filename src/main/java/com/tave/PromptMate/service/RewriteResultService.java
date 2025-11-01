package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.repository.PromptRepository;
import com.tave.PromptMate.repository.RewriteResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RewriteResultService {

    private final RewriteResultRepository rewriteResultRepository;
    private final PromptRepository promptRepository;

    // 결과 저장하기
    public Long saveResult(Long promptId,
                           String modelName,
                           String content,
                           Integer inputTokens,
                           Integer outputTokens,
                           Long latencyMs){

        // 프롬프트 존재 여부 확인하기
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));

        // RewriteResult 엔티티 생성 및 저장하기
        RewriteResult result = RewriteResult.builder()
                .prompt(prompt)
                .modelName(modelName)
                .inputTokens(inputTokens)
                .outputTokens(outputTokens)
                .latencyMs(latencyMs)
                .content(content)
                .build();
        RewriteResult saved = rewriteResultRepository.save(result);
        return saved.getId();
    }

    // 프롬프트별 리라이트 전체 결과 확인
    @Transactional(readOnly = true)
    public List<RewriteResult> getResultsByPrompt(Long promptId){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: "+ promptId));
        return rewriteResultRepository.findAllByPromptIdOrderByCreatedAtDesc(promptId);
    }

    // 프롬프트별 최신 1건 조회
    @Transactional(readOnly = true)
    public RewriteResult getLatestByPrompt(Long promptId){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));
        return rewriteResultRepository.findTop1ByPromptIdOrderByCreatedAtDesc(promptId)
                .orElseThrow(()-> new NotFoundException("rewrite result not found for prompt: "+ promptId ));
    }

    // 리라이트 결과 전체 삭제하기
    public void deleteAllByPrompt(Long promptId){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new NotFoundException("prompt not found: " + promptId));
        rewriteResultRepository.deleteAllByPrompt(prompt);
    }
}
