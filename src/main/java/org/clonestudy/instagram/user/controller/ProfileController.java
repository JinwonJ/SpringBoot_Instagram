package org.clonestudy.instagram.user.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.global.common.ApiResponse;
import org.clonestudy.instagram.user.dto.ProfileResponse;
import org.clonestudy.instagram.user.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 조회
     * GET /api/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ApiResponse<ProfileResponse> profile(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(profileService.getProfile(principal.userId(), userId));
    }
}
