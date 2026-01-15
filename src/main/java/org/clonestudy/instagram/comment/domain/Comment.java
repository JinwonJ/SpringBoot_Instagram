package org.clonestudy.instagram.comment.domain;

import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name="comments", indexes = {
        @Index(name="idx_comments_post_created", columnList = "post_id, createdAt")
})
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="author_id")
    private User author;

    @Column(nullable=false, length=1000)
    private String content;

    @Column(nullable=false)
    private Instant createdAt;

    @Column(nullable=false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}