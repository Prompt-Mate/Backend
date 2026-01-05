package com.tave.PromptMate;

import com.tave.PromptMate.config.RewriteAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(RewriteAiProperties.class)
public class PromptMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromptMateApplication.class, args);
	}
}
