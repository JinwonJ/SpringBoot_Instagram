package org.clonestudy.instagram.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private Long userId;
    private String username;

    private long followerCount;
    private long followingCount;
    private boolean followedByMe;
}
