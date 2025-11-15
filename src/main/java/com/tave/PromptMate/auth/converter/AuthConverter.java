package com.tave.PromptMate.auth.converter;

import com.tave.PromptMate.auth.dto.response.JwtLoginResponse;
import com.tave.PromptMate.domain.User;

public class AuthConverter {

    public static JwtLoginResponse toJwtLoginResponse(User user, String accessToken, String refreshToken){
        return JwtLoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickname())
                .jwtAccessToken("Bearer "+accessToken)
                .jwtRefreshToken(refreshToken)
                .build();
    }
}
