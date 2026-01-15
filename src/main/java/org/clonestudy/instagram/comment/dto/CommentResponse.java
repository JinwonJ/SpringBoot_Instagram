package org.clonestudy.instagram.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long authorId,
        String authorUsername,
        String content,
        Instant createdAt,
        Instant updatedAt
) {}