package com.andrei.demo.model;

public record LoginResponse(
        Boolean success,
        String accessToken,
        String refreshToken,
        String role,
        String errorMessage
) {
}
