package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.common.UserException;
import com.tave.PromptMate.domain.User;
import com.tave.PromptMate.dto.user.NicknameResponse;
import com.tave.PromptMate.redis.service.RefreshTokenRedisService;
import com.tave.PromptMate.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

@Transactional
public NicknameResponse changeNickname(Long userId, String nickname){
        User user=userRepository.findById(userId).orElseThrow(()->new UserException("User not found: " +userId));

        if (userRepository.existsByNickname(nickname)){
            throw new UserException("Nickname already in use: "+nickname);
        }
        user.changeNickname(nickname);
        userRepository.save(user);

        return new NicknameResponse(user.getId(),user.getNickname());
    }
}