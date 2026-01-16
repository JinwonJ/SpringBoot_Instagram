package org.clonestudy.instagram.follow.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.follow.domain.Follow;
import org.clonestudy.instagram.follow.dto.FollowStatusResponse;
import org.clonestudy.instagram.follow.dto.FollowUserResponse;
import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowStatusResponse follow(Long meId, Long targetUserId) {
        validateNotSelf(meId, targetUserId);

        User me = userRepository.findById(meId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 유저가 존재하지 않습니다."));

        try {
            followRepository.save(Follow.of(me, target));
        } catch (DataIntegrityViolationException e) {
            // unique constraint로 이미 팔로우 상태면 여기로 올 수 있음
        }

        return status(meId, targetUserId);
    }

    @Transactional
    public FollowStatusResponse unfollow(Long meId, Long targetUserId) {
        validateNotSelf(meId, targetUserId);

        followRepository.findByFollowerIdAndFollowingId(meId, targetUserId)
                .ifPresent(followRepository::delete);

        return status(meId, targetUserId);
    }

    @Transactional(readOnly = true)
    public FollowStatusResponse status(Long meId, Long targetUserId) {
        boolean following = followRepository.existsByFollowerIdAndFollowingId(meId, targetUserId);
        long followerCount = followRepository.countByFollowingId(targetUserId);
        long followingCount = followRepository.countByFollowerId(targetUserId);

        return FollowStatusResponse.builder()
                .userId(targetUserId)
                .following(following)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse> followers(Long targetUserId, int size) {
        var pageable = PageRequest.of(0, Math.min(size, 50));
        return followRepository.findByFollowingIdOrderByIdDesc(targetUserId, pageable)
                .stream()
                .map(f -> FollowUserResponse.from(f.getFollower()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse> followings(Long targetUserId, int size) {
        var pageable = PageRequest.of(0, Math.min(size, 50));
        return followRepository.findByFollowerIdOrderByIdDesc(targetUserId, pageable)
                .stream()
                .map(f -> FollowUserResponse.from(f.getFollowing()))
                .toList();
    }

    private void validateNotSelf(Long meId, Long targetUserId) {
        if (meId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
    }
}
