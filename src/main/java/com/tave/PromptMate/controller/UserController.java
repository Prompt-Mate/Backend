package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "사용자 API")
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    @Operation(summary = "인증된 사용자 확인", description = "로그인한 사용자를 확인합니다.")
    public ResponseEntity<String> getAuthenticatedUserInfo(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId=principal.getUserId();

        return ResponseEntity.ok("인증된 사용자 ID: " + userId);
    }
}
