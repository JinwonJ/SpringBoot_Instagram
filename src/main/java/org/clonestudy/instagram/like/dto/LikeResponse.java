package org.clonestudy.instagram.like.dto;

public record LikeResponse(
        Long postId,
        long likeCount,
        boolean likedByMe
) {}
