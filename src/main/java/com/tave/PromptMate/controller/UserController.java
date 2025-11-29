package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.dto.user.NicknameRequest;
import com.tave.PromptMate.dto.user.NicknameResponse;
import com.tave.PromptMate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "사용자 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "인증된 사용자 확인", description = "로그인한 사용자를 확인합니다.")
    public ResponseEntity<String> getAuthenticatedUserInfo(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId=principal.getUserId();

        return ResponseEntity.ok("인증된 사용자 ID: " + userId);
    }

    @PatchMapping("/nickname")
    @Operation(summary = "사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    public ResponseEntity<NicknameResponse> changeNickname(@AuthenticationPrincipal CustomUserDetails principal,
                                           @RequestBody NicknameRequest dto){
        Long userId=principal.getUserId();

        NicknameResponse response=userService.changeNickname(userId,dto.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    @PostMapping("/logout")
    @Operation(summary = "로그아웃" ,description = "사용자를 로그아웃합니다.")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails principal){
        Long userId=principal.getUserId();
        String message=userService.logout(userId);
        return ResponseEntity.ok(message);
    }
}
