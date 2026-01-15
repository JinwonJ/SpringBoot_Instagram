package org.clonestudy.instagram.post.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.service.FeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserFeedController {

    private final FeedService feedService;

    // ✅ 특정 유저(프로필) 피드: 인증 없이도 접근 가능하게(공개 계정 가정)
    // 필요하면 나중에 Security에서 인증 요구로 바꿀 수 있음
    @GetMapping("/{userId}/posts")
    public ApiResponse<FeedResponse> userFeed(
            @PathVariable Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ApiResponse.ok(feedService.getUserFeed(userId, cursor, size));
    }
}
