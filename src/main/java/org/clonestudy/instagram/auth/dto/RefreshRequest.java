package org.clonestudy.instagram.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String refreshToken,
        @NotBlank String deviceId
) {}
