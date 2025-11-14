package com.tave.PromptMate.redis.repository;

import com.tave.PromptMate.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
