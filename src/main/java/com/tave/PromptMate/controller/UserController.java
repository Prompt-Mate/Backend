package com.tave.PromptMate.controller;

import com.tave.PromptMate.auth.dto.request.CustomUserDetails;
import com.tave.PromptMate.auth.dto.request.LoginRequest;
import com.tave.PromptMate.auth.dto.request.SignUpRequest;
import com.tave.PromptMate.auth.dto.response.LoginResponse;
import com.tave.PromptMate.auth.service.AuthService;
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

import javax.swing.*;

@RestController
@Tag(name = "사용자 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;



    @PatchMapping("/nickname")
    @Operation(summary = "사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    public ResponseEntity<NicknameResponse> changeNickname(@AuthenticationPrincipal CustomUserDetails principal,
                                           @RequestBody NicknameRequest dto) {
        Long userId = principal.getUserId();

        NicknameResponse response = userService.changeNickname(userId, dto.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @Operation(summary = "회원탈퇴", description = "사용자를 탈퇴합니다.")
    @DeleteMapping
    public ResponseEntity<String>deleteUser(@AuthenticationPrincipal CustomUserDetails principal){

        Long userId=principal.getUserId();
        userService.delete(userId);

        return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴 완료");
    }

}
