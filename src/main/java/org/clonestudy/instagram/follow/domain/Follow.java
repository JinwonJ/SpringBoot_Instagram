package org.clonestudy.instagram.follow.domain;


import org.clonestudy.instagram.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name="follows",
        uniqueConstraints = @UniqueConstraint(name="uk_follow_pair", columnNames={"follower_id","following_id"}))
public class Follow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="follower_id")
    private User follower;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="following_id")
    private User following;

    @Column(nullable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
