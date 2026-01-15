package org.clonestudy.instagram.follow.controller;

import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.follow.service.FollowService;
import org.clonestudy.instagram.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ApiResponse<Void> follow(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long userId) {
        followService.follow(principal.userId(), userId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{userId}/follow")
    public ApiResponse<Void> unfollow(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long userId) {
        followService.unfollow(principal.userId(), userId);
        return ApiResponse.ok();
    }
}