package org.clonestudy.instagram.follow.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.follow.dto.FollowStatusResponse;
import org.clonestudy.instagram.follow.dto.FollowUserResponse;
import org.clonestudy.instagram.follow.service.FollowService;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/follow")
    public ApiResponse<FollowStatusResponse> follow(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(followService.follow(principal.userId(), userId));
    }

    /**
     * 언팔로우
     * DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/follow")
    public ApiResponse<FollowStatusResponse> unfollow(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(followService.unfollow(principal.userId(), userId));
    }

    /**
     * 팔로우 상태 + 카운트
     * GET /api/users/{userId}/follow
     */
    @GetMapping("/follow")
    public ApiResponse<FollowStatusResponse> status(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(followService.status(principal.userId(), userId));
    }

    /**
     * 팔로워 목록
     * GET /api/users/{userId}/followers?size=20
     */
    @GetMapping("/followers")
    public ApiResponse<List<FollowUserResponse>> followers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(followService.followers(userId, size));
    }

    /**
     * 팔로잉 목록
     * GET /api/users/{userId}/followings?size=20
     */
    @GetMapping("/followings")
    public ApiResponse<List<FollowUserResponse>> followings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(followService.followings(userId, size));
    }
}
