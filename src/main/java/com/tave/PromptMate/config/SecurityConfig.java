package com.tave.PromptMate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")                                 // 전체 매칭
                .authorizeHttpRequests(a -> a.anyRequest().permitAll()) // 모두 허용
                .csrf(c -> c.disable())
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())
                .logout(l -> l.disable())
                .build();
    }
}
