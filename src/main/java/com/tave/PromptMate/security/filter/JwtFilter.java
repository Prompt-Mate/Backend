package com.tave.PromptMate.security.filter;

import com.tave.PromptMate.redis.entity.RefreshToken;
import com.tave.PromptMate.redis.service.RefreshTokenRedisService;
import com.tave.PromptMate.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {  //filter chain 요청에 담긴 JWT를 검증하기 위함

    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //토큰 검증
        //request에서 key값을 가지는 header를 뽑아와서 String에 담음
        String authorization = request.getHeader("Authorization");

        //authorization 변수 token이 담겼는지
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken = authorization.substring(7);

        log.debug("Access token from request: {}", accessToken);

        try {
            accessToken = validateAndReissue(response, accessToken);

            if (accessToken != null) {
                Authentication authentication = jwtUtil.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set for user: {}", authentication.getName());
            }

        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    //유효한 토큰(재발급된 토큰, 또는 원본 토큰), 실패 시 null return
    private String validateAndReissue(HttpServletResponse response, String accessToken) {
        // 토큰이 만료되지 않았으면 그대로 반환
        if (!jwtUtil.isExpired(accessToken)) {
            return accessToken;
        }

        log.debug("Access token is expired. Attempting to reissue...");

        // userId 추출
        String userId = jwtUtil.getSubject(accessToken);
        if (userId == null) {
            log.warn("Cannot extract userId from expired token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        // Redis에서 Refresh Token 조회
        Optional<RefreshToken> optionalRefreshToken =
                refreshTokenRedisService.findRefreshToken(Long.parseLong(userId));

        // Refresh Token이 없으면 (로그아웃되었거나 만료)
        if (optionalRefreshToken.isEmpty()) {
            log.debug("Refresh token not found. User needs to login again.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        // Refresh Token 유효성 확인
        String refreshToken = optionalRefreshToken.get().getRefreshToken();
        if (jwtUtil.isExpired(refreshToken)) {
            log.debug("Refresh token is also expired. User needs to login again.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        // 새 Access Token 발급
        String newAccessToken = jwtUtil.reissueWithRefresh(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        log.debug("New access token issued for user: {}", userId);

        return newAccessToken;  // ← 핵심! 새 토큰 반환
    }
}
