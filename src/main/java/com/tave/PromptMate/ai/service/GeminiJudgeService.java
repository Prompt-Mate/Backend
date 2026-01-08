package com.tave.PromptMate.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.PromptMate.ai.dto.GeminiJudgeResponse;
import com.tave.PromptMate.common.UserException;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiJudgeService {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public GeminiJudgeResponse judgePrompt(Long userId, String prompt) {
        User user=getUserById(userId);
        String judgePrompt=buildJudgePrompt(prompt);

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", judgePrompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.0,
                            "topP", 1.0,
                            "topK", 1,
                            "maxOutputTokens", 2048,
                            "responseMimeType", "application/json"
                    )
            );

            String response = geminiWebClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash-lite:generateContent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 503,
                            res -> Mono.error(new RuntimeException("Gemini 일시적 장애 (503)"))
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini API 응답 - userId: {}", userId);
            return parseResponse(response);

        } catch (Exception e) {
            log.error("Gemini API 호출 실패 - userId: {}", userId, e);
            throw new RuntimeException("프롬프트 평가 중 오류가 발생했습니다.", e);
        }
    }

    private GeminiJudgeResponse parseResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            String text = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // JSON 부분만 추출
            int startIdx = text.indexOf("{");
            int endIdx = text.lastIndexOf("}");

            if (startIdx == -1 || endIdx == -1) {
                throw new RuntimeException("응답에서 JSON을 찾을 수 없습니다.");
            }

            String jsonStr = text.substring(startIdx, endIdx + 1);
            return objectMapper.readValue(jsonStr, GeminiJudgeResponse.class);

        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", response, e);
            throw new RuntimeException("응답 파싱 중 오류가 발생했습니다.", e);
        }
    }


    private String buildJudgePrompt(String userPrompt){
        return """
            너는 사용자가 작성한 프롬프트의 품질을 평가하는 전문가이다.
            답변 내용이 아니라 프롬프트 자체만 평가한다.
            
            출력은 반드시 JSON 객체 1개만 작성한다.
            JSON 앞뒤로 어떤 문자도 출력하지 않는다.
            
            ================================================================
            # 점수 계산 원칙 (내부 규칙)
            ================================================================
            - 모든 항목은 기본 점수 100점에서 시작한다.
            - 아래 체크리스트를 기준으로 내부적으로 감점을 누적한다.
            - 감점 규칙, 예/아니오 판단, 수치는 출력에 절대 노출하지 않는다.
            
            ================================================================
            # 내부 판단 체크리스트 (출력 금지)
            ================================================================
            [명확성]
            - 목표를 한 문장으로 요약할 수 있는가
            - 여러 해석 가능성이 존재하는가
            - 지시 대상이 분명한가
            
            [구체성]
            - 누가 / 무엇을이 명시되었는가
            - 출력 형태가 지정되었는가
            - 분량 또는 범위 제한이 있는가
            - 제약 조건이 존재하는가
            
            [구성]
            - 핵심 요구사항이 앞부분에 정리되어 있는가
            - 읽는 순서대로 이해 가능한가
            
            [언어 품질]
            - 문법적으로 자연스러운가
            - 모호한 지시어가 없는가
            - 불필요한 추상 표현이 없는가
            
            [일관성]
            - 서로 충돌하는 요구가 없는가
            - 하나의 기준으로 답변 가능한가
            
            ================================================================
            # 내부 판단 규칙 노출 금지 (매우 중요)
            ================================================================
            아래 항목을 어떤 필드에서도 절대 언급하지 마라.
            - 체크리스트 문장
            - 예 / 아니오 판단
            - 감점 수치 또는 계산 과정
            - 질문 형태의 문장
            - 평가 기준을 그대로 옮긴 표현
            
            코멘트는 반드시
            사람에게 설명하듯 자연스러운 결과 요약 문장으로 작성한다.
            
            ================================================================
            # 한국어 전용 규칙
            ================================================================
            - 모든 코멘트는 100% 자연스러운 한국어로 작성한다.
            - 영어 단어, 약어, 기호 표현을 사용하지 않는다.
            - "일부", "약간", "조금", "다소", "애매함", "부분적으로" 같은 표현을 쓰지 않는다.
            - 모든 지적은 구체적이고 실행 가능해야 한다.
            
            ================================================================
            # 원문 의도 보존 규칙
            ================================================================
            - 원문 프롬프트의 의도를 변경하거나 확장하지 마라.
            - 새로운 목적이나 작업을 제안하지 마라.
            - 대상 독자나 상황을 임의로 추가하지 마라.
            
            ================================================================
            # overall_score 계산
            ================================================================
            overall_score는
            clarity_score, specificity_score, structure_score,
            language_score, consistency_score의 평균을 반올림하여 계산한다.
            
            ================================================================
            # summary_feedback 작성 규칙 (형식 강제)
            ================================================================
            summary_feedback은 반드시 두 문장으로 작성한다.
            - 첫 문장: 가장 큰 문제를 결과 중심으로 설명한다.
            - 두 번째 문장: 사용자가 바로 취할 수 있는 수정 행동을 제시한다.
            내부 판단 기준은 절대 언급하지 않는다.
            
            ================================================================
            # JSON 출력 형식 (반드시 이 구조만 사용)
            ================================================================
            {{
              "overall_score": <0-100>,
              "clarity_score": <0-100>,
              "specificity_score": <0-100>,
              "structure_score": <0-100>,
              "language_score": <0-100>,
              "consistency_score": <0-100>,
              "clarity_comment": "<명확성에 대한 결과 요약>",
              "specificity_comment": "<구체성에 대한 결과 요약>",
              "structure_comment": "<구성에 대한 결과 요약>",
              "language_comment": "<언어 품질에 대한 결과 요약>",
              "consistency_comment": "<일관성에 대한 결과 요약>",
              "summary_feedback": "<문제 1문장 + 해결 행동 1문장>"
            }}
            
            ================================================================
            # 평가 대상 프롬프트
            """+userPrompt;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("user not found" + userId));
    }
}
