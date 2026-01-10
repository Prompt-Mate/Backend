package com.tave.PromptMate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ai.judge")
public class JudgeProperties {
    private String python;
    private String runner;
    private String workdir;
    private int timeoutSeconds = 120;
}
