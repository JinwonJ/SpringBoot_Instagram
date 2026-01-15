package org.clonestudy.instagram.post.controller;


import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.post.dto.*;
import org.clonestudy.instagram.post.service.PostLikeService;
import org.clonestudy.instagram.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> create(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestPart(required = false) String caption,
            @RequestPart List<MultipartFile> images
    ) {
        return ApiResponse.ok(postService.create(principal.userId(), caption, images));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> get(@PathVariable Long postId) {
        return ApiResponse.ok(postService.get(postId));
    }

    @PostMapping("/{postId}/like-toggle")
    public ApiResponse<PostLikeResponse> likeToggle(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postLikeService.toggle(principal.userId(), postId));
    }

    @GetMapping("/{postId}/like")
    public ApiResponse<PostLikeResponse> likeStatus(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postLikeService.status(principal.userId(), postId));
    }
}