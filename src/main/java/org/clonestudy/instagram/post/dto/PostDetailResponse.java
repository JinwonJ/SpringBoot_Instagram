package org.clonestudy.instagram.post.dto;

import java.time.Instant;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        Long authorId,
        String authorUsername,
        String caption,
        List<String> imageUrls,
        Instant createdAt,
        boolean liked,
        long likeCount
) {}
