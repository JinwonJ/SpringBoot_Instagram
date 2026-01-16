package org.clonestudy.instagram.post.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.service.FeedService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserFeedController {

    private final FeedService feedService;

    /**
     * 특정 유저(프로필) 피드
     * GET /api/users/{userId}/posts?cursor=...&size=...
     */
    @GetMapping("/{userId}/posts")
    public ApiResponse<FeedResponse> userPosts(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long meId = principal.userId();
        return ApiResponse.ok(feedService.getUserFeed(meId, userId, cursor, size));
    }
}
