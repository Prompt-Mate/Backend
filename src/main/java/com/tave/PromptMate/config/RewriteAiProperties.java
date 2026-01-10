package com.tave.PromptMate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "ai.rewrite")
public class RewriteAiProperties {
    private String baseUrl;
    private String path;
    private int timeoutMs;
}
