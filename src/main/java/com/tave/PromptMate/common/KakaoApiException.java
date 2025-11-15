package com.tave.PromptMate.common;

public class KakaoApiException extends RuntimeException {
    public KakaoApiException(String message) {
        super(message);
    }

    public KakaoApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
