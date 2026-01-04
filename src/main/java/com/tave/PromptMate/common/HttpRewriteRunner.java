package com.tave.PromptMate.common;

import com.tave.PromptMate.config.RewriteAiProperties;
import com.tave.PromptMate.dto.rewrite.AiRewriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class HttpRewriteRunner implements RewriteRunner{
    private final RestTemplate restTemplate;
    private final RewriteAiProperties props;

    @Override
    public AiRewriteResult run(String beforeText) {
        long start = System.currentTimeMillis();

        String url =
                props.getBaseUrl().replaceAll("/$", "")
                        + "/"
                        + props.getPath().replaceAll("^/", "");


        System.out.println("AI rewrite url = " + url);

        Map<String, Object> body = Map.of("prompt", beforeText == null ? "" : beforeText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try{
            System.out.println("AI rewrite url = " + url);
            System.out.println("AI rewrite timeoutMs = " + props.getTimeoutMs());

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            long latency = System.currentTimeMillis() - start;

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("AI rewrite server invalid response: " + response.getStatusCode());
            }

            Object rewriteFinal = response.getBody().get("rewrite_final");
            if (rewriteFinal == null) {
                throw new RuntimeException("AI rewrite response missing 'rewrite_final'");
            }
            return new AiRewriteResult(
                    rewriteFinal.toString(),
                    0,
                    0,
                    latency,
                    "RewriteModel",
                    "v1"
            );
        } catch (RestClientException e){
            e.printStackTrace();
            throw new RuntimeException("AI rewrite server call failed", e);
        }
    }
}
