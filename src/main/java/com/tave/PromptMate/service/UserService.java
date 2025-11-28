package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.redis.service.RefreshTokenRedisService;
import com.tave.PromptMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RefreshTokenRedisService refreshTokenRedisService;
    private final UserRepository userRepository;

    public String logout(Long userId){
        boolean isDeleted= refreshTokenRedisService.deleteRefreshToken(userId);
        if (isDeleted){
            return "Logout successful";
        }
        else{
            return "Logout failed: User not found";
        }
    }

    @Transactional
    public void delete(Long userId) {

        //1. Redis에서 RefreshToken 삭제
        refreshTokenRedisService.deleteRefreshToken(userId);

        //2. DB에서 사용자 삭제
        User user= userRepository.findById(userId).orElseThrow(()->new NotFoundException("User not found: "+userId));
        userRepository.deleteById(userId);

        log.info("User deleted: {}", userId);
    }
}
