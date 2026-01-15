package org.clonestudy.instagram.post.domain;

import org.clonestudy.instagram.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id") // (선택) 컬럼명 명시 추천
    private User author;

    @Column(length = 2200)
    private String caption;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImage> images = new ArrayList<>();

    // ✅ 추가
    @Column(nullable = false)
    private Instant createdAt;

    // ✅ insert 직전에 자동으로 시간 세팅
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public void addImage(String url) {
        images.add(PostImage.builder().post(this).imageUrl(url).build());
    }
}
