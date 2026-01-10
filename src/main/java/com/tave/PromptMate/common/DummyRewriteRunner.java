package com.tave.PromptMate.common;

import com.tave.PromptMate.dto.rewrite.AiRewriteResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class DummyRewriteRunner implements RewriteRunner {

    @Override
    public AiRewriteResult run(String beforeText) {
        long start = System.currentTimeMillis();

        String rewritten = "[REWRITTEN] " + (beforeText == null ? "" : beforeText.trim());

        long latency = System.currentTimeMillis() - start;

        return new AiRewriteResult(
                rewritten,
                0,      // inputTokens
                0,      // outputTokens
                latency,
                "dummy", // modelName
                "v1"     // version (← 6번째 추가)
        );

    }
}
