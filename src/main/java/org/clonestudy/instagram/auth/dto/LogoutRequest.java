package org.clonestudy.instagram.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank String refreshToken,
        @NotBlank String deviceId
) {}
