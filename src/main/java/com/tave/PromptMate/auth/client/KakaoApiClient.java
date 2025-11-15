package com.tave.PromptMate.auth.client;

import com.tave.PromptMate.auth.dto.response.KakaoTokenResponse;
import com.tave.PromptMate.auth.dto.response.KakaoUserInfo;
import com.tave.PromptMate.common.KakaoApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KakaoApiClient {

    @Value("${kakao.oauth.client-id}")
    private String kakaoClientId;

    @Value("${kakao.oauth.redirect-uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate;


    public KakaoApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    //엑세스 토큰으로 카카오 사용자 정보 조회
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            //1. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            //2. HTTP 요청 엔티티 생성
            HttpEntity<Void> request = new HttpEntity<>(headers);

            //3. API 호출
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    KakaoUserInfo.class
            );

            if (response.getBody() == null) {
                throw new KakaoApiException("카카오 사용자 정보 응답이 비어있습니다.");
            }
            return response.getBody();
        }catch (RestClientException e){
            log.error("카카오 사용자 정보 조회 실패", e);
            throw new KakaoApiException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
    }

    //프론트에서 받은 인가코드->카카오에 토큰발급 요청->엑세스 토큰 발급
    public String requestAccessToken(String authorizationCode) {
        try{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    request,
                    KakaoTokenResponse.class
            );

            log.info("카카오 토큰 발급 성공: {}", response.getStatusCode());

            if (response.getBody() == null || response.getBody().getAccessToken() == null) {
                throw new KakaoApiException("카카오 액세스 토큰이 비어있습니다.");
            }

            return response.getBody().getAccessToken();
        } catch (RestClientException e) {
            log.error("카카오 액세스 토큰 요청 실패", e);
            throw new KakaoApiException("카카오 액세스 토큰 발급에 실패했습니다.", e);
        }
    }

}
