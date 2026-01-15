package org.clonestudy.instagram.follow.repo;

import org.clonestudy.instagram.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    void deleteByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    long countByFollower_Id(Long followerId);   // 내가 팔로우하는 수 (following)
    long countByFollowing_Id(Long userId);      // 나를 팔로우하는 수 (followers)

    // ✅ 추가: 내가 팔로우하는 사람들의 userId 리스트
    @Query("select f.following.id from Follow f where f.follower.id = :followerId")
    List<Long> findFollowingIds(@Param("followerId") Long followerId);
}
