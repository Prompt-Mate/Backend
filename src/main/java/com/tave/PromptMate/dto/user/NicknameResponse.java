package com.tave.PromptMate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicknameResponse {

    private Long userId;
    private String nickname;
}
