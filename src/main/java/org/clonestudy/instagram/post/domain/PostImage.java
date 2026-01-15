package org.clonestudy.instagram.post.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name="post_images")
public class PostImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private Post post;

    @Column(nullable=false, length=500)
    private String imageUrl;
}
