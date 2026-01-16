package org.clonestudy.instagram.comment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_post_created", columnList = "post_id, created_at")
        }
)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 500)
    private String content;

    // ✅ 생성 시 자동 세팅
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ 생성/수정 시 자동 세팅
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void validateOwner(Long userId) {
        if (!author.getId().equals(userId)) {
            throw new IllegalStateException("댓글 작성자만 수정/삭제할 수 있습니다.");
        }
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("댓글 내용은 500자를 초과할 수 없습니다.");
        }
        this.content = content;
        // ❗ updatedAt은 @UpdateTimestamp가 자동 처리
    }
}
