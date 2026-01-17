package com.tave.PromptMate.auth.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.auth.dto.request.LoginRequest;
import com.tave.PromptMate.auth.dto.request.SignUpRequest;
import com.tave.PromptMate.auth.dto.response.JwtLoginResponse;
import com.tave.PromptMate.auth.dto.response.TokenResponse;
import com.tave.PromptMate.auth.dto.response.LoginResponse;
import com.tave.PromptMate.auth.service.AuthService;
import com.tave.PromptMate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "로그인 및 인증 관련 API")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오로그인", description = "카카오로그인을 합니다.")
    public ResponseEntity<JwtLoginResponse> kakaoLogin(@RequestParam String code){
        JwtLoginResponse jwtLoginResponse= authService.loginOrRegister(code);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, jwtLoginResponse.getJwtAccessToken())
                .body(jwtLoginResponse);
    }


    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일을 통해 회원가입합니다.")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request){
        authService.signUp(request);

        return ResponseEntity.status(HttpStatus.OK).body("회원가입 완료");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원가입한 이메일을 통해 로그인합니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response =authService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃" ,description = "사용자를 로그아웃합니다.")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails principal){
        Long userId=principal.getUserId();
        String message=userService.logout(userId);
        return ResponseEntity.ok(message);
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
