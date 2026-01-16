package org.clonestudy.instagram.follow.dto;

import lombok.Builder;
import lombok.Getter;
import org.clonestudy.instagram.user.domain.User;

@Getter
@Builder
public class FollowUserResponse {
    private Long userId;
    private String username; // 네 User 필드명에 맞게 수정

    public static FollowUserResponse from(User user) {
        return FollowUserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
