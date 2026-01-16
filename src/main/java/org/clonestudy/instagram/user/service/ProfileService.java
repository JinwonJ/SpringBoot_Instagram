package org.clonestudy.instagram.user.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.dto.ProfileResponse;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long meId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        boolean followedByMe = false;
        if (meId != null && !meId.equals(userId)) {
            followedByMe = followRepository.existsByFollowerIdAndFollowingId(meId, userId);
        }

        return ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .followedByMe(followedByMe)
                .build();
    }
}
