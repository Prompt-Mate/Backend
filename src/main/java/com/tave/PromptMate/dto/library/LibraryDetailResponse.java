package com.tave.PromptMate.dto.library;

import com.tave.PromptMate.domain.Platform;
import com.tave.PromptMate.domain.PromptCategory;

import java.time.LocalDateTime;

public record LibraryDetailResponse(
        // Library 기본 정보
        Long id,
        Long userId,
        String savedTitle,
        Platform platform,
        PromptCategory category,
        String imageUrl,
        LocalDateTime createdAt,

        // 리라이팅 결과 정보
        Long rewriteResultId,
        String rewrittenContent,

        // 원본 프롬프트 정보
        String originalPrompt,

        // Judge 평가 정보
        JudgeInfo judge
) {
    public record JudgeInfo(
            Long id,
            Integer overallScore,
            Integer clarityScore,
            Integer specificityScore,
            Integer structureScore,
            Integer languageScore,
            Integer consistencyScore,
            String clarityComment,
            String specificityComment,
            String structureComment,
            String languageComment,
            String consistencyComment,
            String summaryFeedback
    ) {}
}
