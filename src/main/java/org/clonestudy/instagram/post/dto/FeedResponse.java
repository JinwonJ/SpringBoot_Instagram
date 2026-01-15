package org.clonestudy.instagram.post.dto;

import java.util.List;

public record FeedResponse(
        List<FeedItemResponse> items,
        String nextCursor // 없으면 null
) {}
