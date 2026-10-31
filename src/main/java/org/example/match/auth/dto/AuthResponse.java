package org.example.match.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken,
        long refreshTokenExpiresIn
) {
}