package org.clonestudy.instagram.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.domain.RefreshToken;
import org.clonestudy.instagram.auth.dto.*;
import org.clonestudy.instagram.auth.jwt.JwtTokenProvider;
import org.clonestudy.instagram.auth.repo.RefreshTokenRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인: access + refresh 발급, refresh는 deviceId 단위로 저장(해시)
    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest http) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String access = tokenProvider.createAccessToken(user.getId(), user.getUsername());
        String refresh = tokenProvider.createRefreshToken(user.getId());

        String deviceId = req.deviceId();

        // 같은 deviceId로 재로그인하면 기존 세션 revoke 후 교체
        refreshTokenRepository.findByUser_IdAndDeviceId(user.getId(), deviceId)
                .ifPresent(existing -> existing.setRevokedAt(Instant.now()));

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .deviceId(deviceId)
                .tokenHash(sha256(refresh))
                .expiresAt(tokenProvider.getExpiry(refresh))
                .userAgent(http.getHeader("User-Agent"))
                .ip(getClientIp(http))
                .lastUsedAt(Instant.now())
                .build());

        return new AuthResponse(access, refresh);
    }

    // refresh: 해당 deviceId 세션만 검증 후 access 재발급 + refresh 회전(rotate)
    @Transactional
    public AuthResponse refresh(RefreshRequest req, HttpServletRequest http) {
        String refresh = req.refreshToken();
        String deviceId = req.deviceId();

        if (!tokenProvider.isRefreshToken(refresh)) {
            throw new IllegalArgumentException("유효하지 않은 refreshToken 입니다.");
        }

        Long userId = tokenProvider.getUserId(refresh);

        RefreshToken session = refreshTokenRepository.findByUser_IdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기기 세션이 없습니다. 다시 로그인하세요."));

        if (session.isRevoked()) throw new IllegalArgumentException("폐기된 refreshToken 입니다. 다시 로그인하세요.");
        if (session.isExpired()) throw new IllegalArgumentException("만료된 refreshToken 입니다. 다시 로그인하세요.");

        // 요청으로 온 refresh가 지금 기기 세션의 refresh와 일치해야 함
        if (!session.getTokenHash().equals(sha256(refresh))) {
            session.setRevokedAt(Instant.now()); // 탈취/불일치 의심 -> 해당 세션 폐기
            throw new IllegalArgumentException("refreshToken 불일치(탈취 의심). 다시 로그인하세요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccess = tokenProvider.createAccessToken(user.getId(), user.getUsername());

        // ✅ refresh 회전: 기존 세션 revoke + 새 refresh 저장
        String newRefresh = tokenProvider.createRefreshToken(userId);
        session.setRevokedAt(Instant.now());

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .deviceId(deviceId)
                .tokenHash(sha256(newRefresh))
                .expiresAt(tokenProvider.getExpiry(newRefresh))
                .userAgent(http.getHeader("User-Agent"))
                .ip(getClientIp(http))
                .lastUsedAt(Instant.now())
                .build());

        return new AuthResponse(newAccess, newRefresh);
    }

    // 현재 기기 로그아웃: refresh 세션 revoke
    @Transactional
    public void logout(LogoutRequest req) {
        // deviceId도 받지만, 안전하게 tokenHash 기반으로 revoke
        refreshTokenRepository.findByTokenHash(sha256(req.refreshToken()))
                .ifPresent(rt -> rt.setRevokedAt(Instant.now()));
    }

    // 내 세션 목록
    @Transactional(readOnly = true)
    public List<SessionResponse> sessions(Long meId) {
        return refreshTokenRepository.findByUser_IdAndRevokedAtIsNull(meId).stream()
                .map(rt -> new SessionResponse(
                        rt.getDeviceId(),
                        rt.getUserAgent(),
                        rt.getIp(),
                        rt.getCreatedAt(),
                        rt.getLastUsedAt(),
                        rt.getExpiresAt()
                ))
                .toList();
    }

    // 특정 기기 삭제(강제 로그아웃)
    @Transactional
    public void revokeDevice(Long meId, String deviceId) {
        RefreshToken rt = refreshTokenRepository.findByUser_IdAndDeviceId(meId, deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 deviceId 세션이 없습니다."));
        rt.setRevokedAt(Instant.now());
    }

    // 모든 기기 로그아웃
    @Transactional
    public void logoutAll(Long meId) {
        Instant now = Instant.now();
        refreshTokenRepository.findByUser_IdAndRevokedAtIsNull(meId)
                .forEach(rt -> rt.setRevokedAt(now));
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
