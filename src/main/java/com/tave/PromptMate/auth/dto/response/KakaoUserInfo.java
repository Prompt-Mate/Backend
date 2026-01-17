package com.tave.PromptMate.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
public class KakaoUserInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private String email;

        @JsonProperty("profile")
        private Profile profile;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
}
