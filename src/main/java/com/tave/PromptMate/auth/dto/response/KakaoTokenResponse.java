package com.tave.PromptMate.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)      // 알 수 없는 필드 무시 (id_token 등)
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;

    @JsonProperty("id_token")
    private String idToken;  // 카카오 API 응답에 포함될 수 있는 필드 (선택적)
}
