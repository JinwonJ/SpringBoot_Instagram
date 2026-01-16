package org.clonestudy.instagram.follow.repo;

import org.clonestudy.instagram.follow.domain.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowerId(Long followerId);   // 내가 팔로잉한 수 (followings)
    long countByFollowingId(Long followingId); // 나를 팔로우한 수 (followers)

//    @Query("select f.follower.id from Follow f where f.following.id = :userId")
//    List<Long> findFollowerIds(@Param("userId") Long userId);
    @Query("select f.following.id from Follow f where f.follower.id = :meId")
    List<Long> findFollowingIds(@Param("meId") Long meId);

    // 팔로잉 목록: followerId가 내가 되고 following이 리스트로 나옴
    List<Follow> findByFollowerIdOrderByIdDesc(Long followerId, Pageable pageable);

    // 팔로워 목록: followingId가 내가 되고 follower가 리스트로 나옴
    List<Follow> findByFollowingIdOrderByIdDesc(Long followingId, Pageable pageable);

}
