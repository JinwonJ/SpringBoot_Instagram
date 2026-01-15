package org.clonestudy.instagram.post.controller;

import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ApiResponse<FeedResponse> feed(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(feedService.getFeed(principal.userId(), cursor, size));
    }
}