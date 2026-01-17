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

    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("이미 사용 중인 username 입니다.");
        }

        User user = User.builder()
                .email(req.email())
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();

        User saved = userRepository.save(user);
        return new SignupResponse(saved.getId(), saved.getEmail(), saved.getUsername());
    }

    // 로그인: access + refresh 발급, refresh는 deviceId 단위로 저장(해시)
    // 추가: PC 1개 + MOBILE 1개만 허용 (타입별 1개)
    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest http) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String deviceId = normalizeDeviceId(req.deviceId());
        DeviceType type = parseDeviceType(deviceId);

        // 타입별 1개 제한: 동일 타입의 기존 활성 세션이 있으면 revoke(교체 로그인)
        enforceOneSessionPerType(user.getId(), type, deviceId);

        String access = tokenProvider.createAccessToken(user.getId(), user.getUsername());
        String refresh = tokenProvider.createRefreshToken(user.getId());

        Instant now = Instant.now();

        // 같은 (userId, deviceId)는 update
        RefreshToken session = refreshTokenRepository.findByUser_IdAndDeviceId(user.getId(), deviceId)
                .orElse(null);

        if (session == null) {
            session = RefreshToken.builder()
                    .user(user)
                    .deviceId(deviceId)
                    .createdAt(now)
                    .build();
        }

        session.setTokenHash(sha256(refresh));
        session.setExpiresAt(tokenProvider.getExpiry(refresh));
        session.setUserAgent(http.getHeader("User-Agent"));
        session.setIp(getClientIp(http));
        session.setLastUsedAt(now);
        session.setRevokedAt(null);

        refreshTokenRepository.save(session);
        return new AuthResponse(access, refresh);
    }

    // refresh: 해당 deviceId 세션만 검증 후 access 재발급 + refresh 회전(rotate)
    @Transactional
    public AuthResponse refresh(RefreshRequest req, HttpServletRequest http) {
        String refresh = req.refreshToken();
        String deviceId = normalizeDeviceId(req.deviceId());

        if (!tokenProvider.isRefreshToken(refresh)) {
            throw new IllegalArgumentException("유효하지 않은 refreshToken 입니다.");
        }

        Long userId = tokenProvider.getUserId(refresh);

        RefreshToken session = refreshTokenRepository.findByUser_IdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기기 세션이 없습니다. 다시 로그인하세요."));

        if (session.isRevoked()) throw new IllegalArgumentException("폐기된 refreshToken 입니다. 다시 로그인하세요.");
        if (session.isExpired()) throw new IllegalArgumentException("만료된 refreshToken 입니다. 다시 로그인하세요.");

        if (!session.getTokenHash().equals(sha256(refresh))) {
            session.setRevokedAt(Instant.now());
            throw new IllegalArgumentException("refreshToken 불일치(탈취 의심). 다시 로그인하세요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccess = tokenProvider.createAccessToken(user.getId(), user.getUsername());

        // refresh 회전: 기존 row 업데이트
        String newRefresh = tokenProvider.createRefreshToken(userId);
        session.setTokenHash(sha256(newRefresh));
        session.setExpiresAt(tokenProvider.getExpiry(newRefresh));
        session.setUserAgent(http.getHeader("User-Agent"));
        session.setIp(getClientIp(http));
        session.setLastUsedAt(Instant.now());
        session.setRevokedAt(null);

        refreshTokenRepository.save(session);

        return new AuthResponse(newAccess, newRefresh);
    }

    // 현재 기기 로그아웃: refresh 세션 revoke
    @Transactional
    public void logout(LogoutRequest req) {
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
        deviceId = normalizeDeviceId(deviceId);

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

    /**
     * 타입별 1개 제한 로직
     * - pc:.* 는 1개
     * - mobile:.* 는 1개
     * - 같은 타입으로 다른 deviceId가 로그인하면 기존을 revoke하고 새 로그인 허용
     */
    private void enforceOneSessionPerType(Long userId, DeviceType type, String newDeviceId) {
        String prefix = switch (type) {
            case PC -> "pc:";
            case MOBILE -> "mobile:";
        };

        // revokedAt is null 인 것들 중 타입 prefix로 찾음
        List<RefreshToken> actives = refreshTokenRepository
                .findByUser_IdAndRevokedAtIsNullAndDeviceIdStartingWith(userId, prefix);

        Instant now = Instant.now();

        for (RefreshToken rt : actives) {
            // 같은 deviceId면 유지(갱신)
            if (rt.getDeviceId().equals(newDeviceId)) continue;

            // 만료된 세션이면 굳이 revoke 안 해도 되지만, 깔끔하게 정리하고 싶으면 revoke
            // (엔티티 isExpired가 expiresAt 기반이면 여기서도 체크 가능)
            // 여기서는 정책적으로 "타입당 하나만 활성"을 유지하기 위해 revoke
            rt.setRevokedAt(now);
        }
    }

    /**
     * deviceId 형식 강제
     * - 허용: "pc:<id>" 또는 "mobile:<id>"
     * - 그 외는 에러
     */
    private String normalizeDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId: 공백일 수 없습니다");
        }
        deviceId = deviceId.trim();

        // 최소 길이 체크(너무 짧은 값 방지)
        if (deviceId.length() < 6) {
            throw new IllegalArgumentException("deviceId 형식이 올바르지 않습니다. 예: pc:xxxxxx / mobile:xxxxxx");
        }

        // prefix 강제
        if (!(deviceId.startsWith("pc:") || deviceId.startsWith("mobile:"))) {
            throw new IllegalArgumentException("deviceId 형식이 올바르지 않습니다. pc: 또는 mobile: 로 시작해야 합니다.");
        }

        return deviceId;
    }

    private DeviceType parseDeviceType(String deviceId) {
        if (deviceId.startsWith("pc:")) return DeviceType.PC;
        return DeviceType.MOBILE;
    }

    private enum DeviceType { PC, MOBILE }

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
