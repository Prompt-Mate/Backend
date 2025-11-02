package com.tave.PromptMate.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {  //filter chain 요청에 담긴 JWT를 검증하기 위함

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //토큰 검증
        //request에서 key값을 가지는 header를 뽑아와서 String에 담음
        String authorization=request.getHeader("Authorization");

        //authorization 변수 token이 담겼는지
        if(authorization==null || !authorization.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken=authorization.split(" ")[1];

        log.debug("Access token from request: {}", accessToken);





    }
}
