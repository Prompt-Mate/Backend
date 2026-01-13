package com.tave.PromptMate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration    //스프링 실행 시 설정파일 읽어드리기 위한 어노테이션
public class SwaggerConfig {

    public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

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
