package com.tave.PromptMate.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiWebClientConfig {

    @Value("${gemini.api.key}")
    private String apikey;

    @Bean
    public WebClient geminiWebClient(WebClient.Builder builder){

        return builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key",apikey)
                .build();
    }

}
