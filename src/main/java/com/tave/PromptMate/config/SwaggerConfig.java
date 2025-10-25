package com.tave.PromptMate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration    //스프링 실행 시 설정파일 읽어드리기 위한 어노테이션
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi(){
        String authHeader = "Authorization";

        Info apiInfo = new Info()
                .title("Back-End API")
                .description("백엔드 API 명세서")
                .version("1.0.0");

        // JWT Security 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(authHeader);
        Components components = new Components()
                .addSecuritySchemes(authHeader,
                        new SecurityScheme()
                                .name(authHeader)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(apiInfo);
    }

}
