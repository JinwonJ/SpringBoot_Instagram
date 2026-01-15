package org.clonestudy.instagram.user.controller;


import org.clonestudy.instagram.auth.jwt.AuthPrincipal;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserMeController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new MeResponse(user.getId(), user.getEmail(), user.getUsername(), user.getBio(), user.getProfileImageUrl());
    }

    public record MeResponse(Long id, String email, String username, String bio, String profileImageUrl) {}
}