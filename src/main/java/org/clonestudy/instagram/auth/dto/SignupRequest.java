package org.clonestudy.instagram.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min=3, max=30) String username,
        @NotBlank @Size(min=8, max=64) String password
) {}