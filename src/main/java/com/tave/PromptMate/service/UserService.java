package com.tave.PromptMate.service;

import com.tave.PromptMate.redis.service.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RefreshTokenRedisService refreshTokenRedisService;

    public String logout(Long userId){
        boolean isDeleted= refreshTokenRedisService.deleteRefreshToken(userId);
        if (isDeleted){
            return "Logout successful";
        }
        else{
            return "Logout failed: User not found";
        }
    }
}
