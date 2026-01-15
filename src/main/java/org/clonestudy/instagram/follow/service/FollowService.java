package org.clonestudy.instagram.follow.service;

import org.clonestudy.instagram.follow.domain.Follow;
import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        if (followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId)) return;

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("Following not found"));

        followRepository.save(Follow.builder()
                .follower(follower)
                .following(following)
                .build());
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        followRepository.deleteByFollower_IdAndFollowing_Id(followerId, followingId);
    }
}