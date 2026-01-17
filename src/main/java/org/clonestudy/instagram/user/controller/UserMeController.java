//package org.clonestudy.instagram.user.controller;
//
//
//import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
//import org.clonestudy.instagram.user.domain.User;
//import org.clonestudy.instagram.user.repo.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserMeController {
//
//    private final UserRepository userRepository;
//
//    @GetMapping("/me")
//    public MeResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
//        User user = userRepository.findById(principal.userId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        return new MeResponse(user.getId(), user.getEmail(), user.getUsername(), user.getBio(), user.getProfileImageUrl());
//    }
//
//    public record MeResponse(Long id, String email, String username, String bio, String profileImageUrl) {}
//}
package org.clonestudy.instagram.user.controller;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.user.service.UserMeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeService userMeService;

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
        // 기존처럼 principal.userId() 기반으로 조회하되, 실제 조회는 Service로 이동
        return userMeService.getMe(principal.userId());
    }

    // ✅ 기존 응답 스키마/필드 그대로 유지
    public record MeResponse(Long id, String email, String username, String bio, String profileImageUrl) {}
}
