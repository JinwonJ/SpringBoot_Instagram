package org.clonestudy.instagram.post.dto;

import java.time.Instant;
import java.util.List;

public record FeedItemResponse(
        Long postId,
        Long authorId,
        String authorUsername,
        String caption,
        List<String> imageUrls,
        Instant createdAt,
        boolean likedByMe,
        long likeCount,
        long commentCount
) {}
