package org.clonestudy.instagram.auth.dto;

public record SignupResponse(
        Long id,
        String email,
        String username
) {}
