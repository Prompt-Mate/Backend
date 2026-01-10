package com.tave.PromptMate.common;

import com.tave.PromptMate.config.RewriteAiProperties;
import com.tave.PromptMate.dto.rewrite.AiRewriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class HttpRewriteRunner implements RewriteRunner {

    private final WebClient rewriteWebClient;
    private final RewriteAiProperties props;

    @Override
    public AiRewriteResult run(String beforeText) {
        long start = System.currentTimeMillis();

        Map<String, Object> body = Map.of("prompt", beforeText == null ? "" : beforeText);

        Map response = rewriteWebClient.post()
                .uri("/" + props.getPath().replaceAll("^/", ""))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .block();

        long latency = System.currentTimeMillis() - start;

        if (response == null || response.get("rewrite_final") == null) {
            throw new RuntimeException("AI rewrite response missing 'rewrite_final'");
        }

        return new AiRewriteResult(
                response.get("rewrite_final").toString(),
                0,
                0,
                latency,
                "RewriteModel",
                "v1"
        );
    }
}
