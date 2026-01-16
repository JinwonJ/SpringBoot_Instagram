package org.clonestudy.instagram.like.domain;

import jakarta.persistence.*;
import lombok.*;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.user.domain.User;

import java.time.Instant;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_user_like", columnNames = {"post_id", "user_id"}),
        indexes = {
                @Index(name = "idx_post_likes_post", columnList = "post_id"),
                @Index(name = "idx_post_likes_user", columnList = "user_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
