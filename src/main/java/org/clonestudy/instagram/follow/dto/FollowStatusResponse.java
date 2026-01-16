package org.clonestudy.instagram.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowStatusResponse {
    private Long userId;            // 대상 유저
    private boolean following;      // 내가 대상 유저를 팔로우 중인지
    private long followerCount;     // 대상 유저의 팔로워 수
    private long followingCount;    // 대상 유저의 팔로잉 수
}
