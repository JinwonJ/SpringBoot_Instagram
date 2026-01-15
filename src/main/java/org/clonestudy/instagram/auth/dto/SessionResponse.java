package org.clonestudy.instagram.auth.dto;

import java.time.Instant;

public record SessionResponse(
        String deviceId,
        String userAgent,
        String ip,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt
) {}
