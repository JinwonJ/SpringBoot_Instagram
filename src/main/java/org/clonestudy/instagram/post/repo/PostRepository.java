package org.clonestudy.instagram.post.repo;

import org.clonestudy.instagram.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 홈 피드 (팔로잉 + 나)
    @Query("""
        select p from Post p
        where p.author.id in :authorIds
          and (
            :cursorTime is null
            or p.createdAt < :cursorTime
            or (p.createdAt = :cursorTime and p.id < :cursorId)
          )
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findFeedPage(
            @Param("authorIds") List<Long> authorIds,
            @Param("cursorTime") Instant cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // ✅ 유저(프로필) 피드: 특정 유저의 게시글만
    @Query("""
        select p from Post p
        where p.author.id = :userId
          and (
            :cursorTime is null
            or p.createdAt < :cursorTime
            or (p.createdAt = :cursorTime and p.id < :cursorId)
          )
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findUserPostsPage(
            @Param("userId") Long userId,
            @Param("cursorTime") Instant cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}


//package org.clonestudy.instagram.post.repo;
//
//
//import org.clonestudy.instagram.post.domain.Post;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.*;
//import org.springframework.data.repository.query.Param;
//
//import java.time.Instant;
//import java.util.List;
//
//public interface PostRepository extends JpaRepository<Post, Long> {
//
//    @Query("""
//        select p from Post p
//        where p.author.id in :authorIds
//          and (
//            :cursorTime is null
//            or p.createdAt < :cursorTime
//            or (p.createdAt = :cursorTime and p.id < :cursorId)
//          )
//        order by p.createdAt desc, p.id desc
//    """)
//    List<Post> findFeedPage(
//            @Param("authorIds") List<Long> authorIds,
//            @Param("cursorTime") Instant cursorTime,
//            @Param("cursorId") Long cursorId,
//            Pageable pageable
//    );
//}