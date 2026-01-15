package org.clonestudy.instagram.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_email", columnNames = "email"),
                @UniqueConstraint(name="uk_users_username", columnNames = "username")
        })
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String email;

    @Column(nullable=false, length=30)
    private String username;

    @Column(nullable=false)
    private String passwordHash;

    @Column(length=200)
    private String bio;

    @Column(length=500)
    private String profileImageUrl;

    @Column(nullable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}