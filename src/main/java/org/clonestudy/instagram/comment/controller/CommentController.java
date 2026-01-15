package org.clonestudy.instagram.comment.controller;

import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.comment.dto.*;
import org.clonestudy.instagram.comment.service.CommentService;
import org.clonestudy.instagram.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CommentResponse> create(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest req
    ) {
        return ApiResponse.ok(commentService.create(principal.userId(), postId, req));
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> list(@PathVariable Long postId) {
        return ApiResponse.ok(commentService.list(postId));
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> update(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest req
    ) {
        return ApiResponse.ok(commentService.update(principal.userId(), commentId, req));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long commentId
    ) {
        commentService.delete(principal.userId(), commentId);
        return ApiResponse.ok();
    }
}