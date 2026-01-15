package com.tave.PromptMate.auth.controller;

import com.tave.PromptMate.auth.dto.request.LoginRequest;
import com.tave.PromptMate.auth.dto.request.SignUpRequest;
import com.tave.PromptMate.auth.dto.response.JwtLoginResponse;
import com.tave.PromptMate.auth.dto.response.TokenResponse;
import com.tave.PromptMate.auth.dto.response.LoginResponse;
import com.tave.PromptMate.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name="카카오로그인 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/auth/login/kakao")
    @Operation(summary = "카카오로그인")
    public ResponseEntity<JwtLoginResponse> kakaoLogin(@RequestParam String code){
        JwtLoginResponse jwtLoginResponse= authService.loginOrRegister(code);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, jwtLoginResponse.getJwtAccessToken())
                .body(jwtLoginResponse);
    }


    //백엔드 확인용
    @PostMapping("/api/auth/token/reissue")
    @Operation(summary = "백엔드 개발용 API")
    public ResponseEntity<TokenResponse> reissueAccessToken(@RequestParam String refreshToken) throws Exception {

        //새 accessToken 재발급 시도
        String newAccessToken = authService.reissueAccessToken(refreshToken);

        TokenResponse response=new TokenResponse(newAccessToken);

        return ResponseEntity.ok(response);
    }

}
