package org.clonestudy.instagram.post.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.post.dto.PostCreateResponse;
import org.clonestudy.instagram.post.dto.PostDetailResponse;
import org.clonestudy.instagram.post.service.PostService;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> create(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestPart(value = "caption", required = false) String caption,
            @RequestPart(value = "images") List<MultipartFile> images
    ) {
        return ApiResponse.ok(postService.create(principal.userId(), caption, images));
    }

    /**
     * 게시글 상세 (liked/likeCount 포함)
     */
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> get(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId
    ) {
        return ApiResponse.ok(postService.get(principal.userId(), postId));
    }
}
