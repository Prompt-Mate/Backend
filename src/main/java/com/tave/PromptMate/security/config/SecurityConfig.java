package com.tave.PromptMate.security.config;

import com.tave.PromptMate.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private static final String[] WHITE_LIST={
            "/login/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/**",
            "/hi"
    };

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //csrf disable: jwt는 세션을 stateless로 관리하기 때문
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((auth)->auth.disable())
                .httpBasic((auth)->auth.disable())
                .formLogin((auth)->auth.disable())
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration=new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://15.164.131.214:8080",
                "http://15.164.131.214:3000"    // ← 프론트 배포 주소 (필요시)
                 ));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

}
