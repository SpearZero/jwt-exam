package com.example.jwt.payload.request;

import javax.validation.constraints.NotBlank;

public class TokenRefreshRequest {

    @NotBlank
        private String refreshToken;

    public TokenRefreshRequest() {}

    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
