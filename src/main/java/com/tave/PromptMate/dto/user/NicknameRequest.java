package com.tave.PromptMate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NicknameRequest {
    @NotBlank
    private String nickname;
}
