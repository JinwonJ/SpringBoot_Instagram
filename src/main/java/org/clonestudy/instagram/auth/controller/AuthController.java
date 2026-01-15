package org.clonestudy.instagram.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.dto.*;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.auth.service.AuthService;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(req, http));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req, HttpServletRequest http) {
        return ApiResponse.ok(authService.refresh(req, http));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req);
        return ApiResponse.ok();
    }

    @GetMapping("/sessions")
    public ApiResponse<List<SessionResponse>> sessions(@AuthenticationPrincipal AuthPrincipal principal) {
        return ApiResponse.ok(authService.sessions(principal.userId()));
    }

    @DeleteMapping("/sessions/{deviceId}")
    public ApiResponse<Void> revokeDevice(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String deviceId
    ) {
        authService.revokeDevice(principal.userId(), deviceId);
        return ApiResponse.ok();
    }

    @PostMapping("/logout-all")
    public ApiResponse<Void> logoutAll(@AuthenticationPrincipal AuthPrincipal principal) {
        authService.logoutAll(principal.userId());
        return ApiResponse.ok();
    }
}
