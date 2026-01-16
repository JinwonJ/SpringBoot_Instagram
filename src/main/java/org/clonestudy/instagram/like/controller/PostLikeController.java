package org.clonestudy.instagram.like.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.like.dto.LikeResponse;
import org.clonestudy.instagram.like.service.PostLikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 좋아요
     * POST /api/posts/{postId}/like
     */
    @PostMapping
    public ApiResponse<LikeResponse> like(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postLikeService.like(principal.userId(), postId));
    }

    /**
     * 좋아요 취소
     * DELETE /api/posts/{postId}/like
     */
    @DeleteMapping
    public ApiResponse<LikeResponse> unlike(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postLikeService.unlike(principal.userId(), postId));
    }

    /**
     * 좋아요 상태/카운트
     * GET /api/posts/{postId}/like
     */
    @GetMapping
    public ApiResponse<LikeResponse> status(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postLikeService.status(principal.userId(), postId));
    }
}
