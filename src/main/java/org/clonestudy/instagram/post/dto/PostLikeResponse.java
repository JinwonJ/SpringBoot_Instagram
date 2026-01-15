package org.clonestudy.instagram.post.dto;

public record PostLikeResponse(
        Long postId,
        boolean liked,
        long likeCount
) {}