package com.tave.PromptMate.security.jwt;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private SecretKey secretKey;
    private final UserRepository userRepository;


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    @PostConstruct
    protected void initSecretKey() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS512.getJcaName());
    }


    public String generateAccessToken(Authentication authentication, String userId) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME, "access",userId);
    }

    public String generateRefreshToken(Authentication authentication, String userId) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME, "refresh", userId);
    }

    //토큰 생성 공통 메서드
    private String generateToken(Authentication authentication,Long expireTime, String category, String userId){
        //저장
        String authorities=authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));  //"ROLE_USER,ROLE_ADMIN"

        long now= System.currentTimeMillis();

        return Jwts.builder()
                .subject(userId)
                .claim("category", category)
                .claim("email",authentication.getName())
                .claim("authorities",authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now+expireTime))
                .signWith(secretKey)
                .compact();
    }

    //토큰 만료 여부 확인. 만료시 true 반환
    public Boolean isExpired(String token){
        try{
            return getClaims(token).getExpiration().before(new Date());
        } catch(ExpiredJwtException e){
            return true;
        }
    }

    public String reissueWithRefresh(String refreshToken) {
        Authentication authentication = getAuthentication(refreshToken);
        String userId = getSubject(refreshToken);
        return generateAccessToken(authentication, userId);
    }

    //토큰에서 Claims추출
    private Claims getClaims(String token){
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String token){
        // 토큰 파싱
        Claims claims=getClaims(token);

        //권한 정보 추출 & 변환
        String authStr=claims.get("authorities",String.class);
        List<GrantedAuthority> authorities = List.of();

        if (authStr  != null && !authStr.isEmpty()) {
            authorities = List.of(authStr.split(","))
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

//        User principal=new User(claims.getSubject(),"",authorities);
        User user =userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(()->new UsernameNotFoundException("user not found"));

        CustomUserDetails principal=new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
    }

    public String getSubject(String token){
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Failed to extract subject from token: {}", e.getMessage());
            return null; // or throw new TokenException(SecurityErrorCode.INVALID_TOKEN);
        }
    }

    //인가된 사용자 꺼내기
    public Authentication getAuthenticationFromUserId(String userId) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(userId, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

}
