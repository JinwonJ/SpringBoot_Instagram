package org.clonestudy.instagram.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.clonestudy.instagram.user.domain.User;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_user_device", columnList = "user_id, deviceId"),
                @Index(name = "idx_refresh_tokens_hash", columnList = "tokenHash", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_device", columnNames = {"user_id", "deviceId"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(nullable = false, length = 100)
    private String deviceId;

    @Column(length = 300)
    private String userAgent;

    @Column(length = 60)
    private String ip;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastUsedAt;

    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (lastUsedAt == null) lastUsedAt = now;
    }

    public boolean isRevoked() { return revokedAt != null; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
