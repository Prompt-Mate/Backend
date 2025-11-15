package com.tave.PromptMate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class PromptMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromptMateApplication.class, args);
	}
}
