package org.clonestudy.instagram.auth.repo;

import org.clonestudy.instagram.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser_IdAndDeviceId(Long userId, String deviceId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUser_IdAndRevokedAtIsNull(Long userId);

    // 특정 prefix로 시작하는 deviceId(=타입) 세션 조회
    List<RefreshToken> findByUser_IdAndRevokedAtIsNullAndDeviceIdStartingWith(Long userId, String prefix);

    // 만료 이전만 active로 보고 싶으면 아래로 바꿔도 됨
    // List<RefreshToken> findByUser_IdAndRevokedAtIsNullAndExpiresAtAfterAndDeviceIdStartingWith(Long userId, Instant now, String prefix);
}
