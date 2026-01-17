package org.clonestudy.instagram.user.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.user.controller.UserMeController;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserMeController.MeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserMeController.MeResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getBio(),
                user.getProfileImageUrl()
        );
    }
}
