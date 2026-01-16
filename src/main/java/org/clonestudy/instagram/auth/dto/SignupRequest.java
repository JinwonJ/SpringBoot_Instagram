package org.clonestudy.instagram.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4, max = 100) String password,
        @NotBlank @Size(min = 2, max = 30) String username
) {}
