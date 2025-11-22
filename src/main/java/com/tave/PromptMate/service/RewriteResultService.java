package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.RewriteResult;
import com.tave.PromptMate.dto.rewrite.CreateRewriteRequest;
import com.tave.PromptMate.dto.rewrite.RewriteMapper;
import com.tave.PromptMate.dto.rewrite.RewriteResponse;
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

    // 리라이팅 결과 생성(저장)하기
    public RewriteResponse create(CreateRewriteRequest req){
        Prompt prompt = promptRepository.findById(req.promptId())
                .orElseThrow(()->new NotFoundException("prompt not found: " + req.promptId()));
        RewriteResult entity = RewriteMapper.toEntity(req, prompt);
        rewriteResultRepository.save(entity);
        return RewriteMapper.toResponse(entity);
    }

    // 프롬프트별 리라이팅 결과 전체 목록 조회
    @Transactional(readOnly = true)
    public List<RewriteResponse> getListByPrompt(Long promptId){
        List<RewriteResult> list = rewriteResultRepository
                .findByPromptIdOrderByCreatedAtDesc(promptId);
        return list.stream()
                .map(RewriteMapper::toResponse)
                .toList();
    }

    // 프롬프트별 최신 1건 조회
    @Transactional(readOnly = true)
    public RewriteResponse getLatestByPrompt(Long promptId){
        RewriteResult entity = rewriteResultRepository
                .findTopByPromptIdOrderByCreatedAtDesc(promptId)
                .orElseThrow(()->new NotFoundException("rewriteResult not found: "+promptId));
        return RewriteMapper.toResponse(entity);
    }

    // 리라이팅 단건 조회
    @Transactional(readOnly = true)
    public RewriteResponse getOne(Long id){
        RewriteResult entity = rewriteResultRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("rewrite not found: "+ id));
        return RewriteMapper.toResponse(entity);
    }

    // 리라이팅 삭제하기
    public void delete(Long id){
        if (!rewriteResultRepository.existsById(id)){
            throw new NotFoundException("rewriteResult not found: "+ id);
        } rewriteResultRepository.deleteById(id);
    }
}
