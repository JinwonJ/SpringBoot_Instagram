package org.clonestudy.instagram.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}