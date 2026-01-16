package org.clonestudy.instagram.post.repo;

import org.clonestudy.instagram.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // =========================
    // 홈 피드 (커서 없음)
    // =========================
    @Query("""
        select p from Post p
        where p.author.id in :authorIds
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findFeedFirstPage(
            @Param("authorIds") List<Long> authorIds,
            Pageable pageable
    );

    // =========================
    // 홈 피드 (커서 있음)
    // =========================
    @Query("""
        select p from Post p
        where p.author.id in :authorIds
          and (
            p.createdAt < :cursorTime
            or (p.createdAt = :cursorTime and p.id < :cursorId)
          )
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findFeedNextPage(
            @Param("authorIds") List<Long> authorIds,
            @Param("cursorTime") Instant cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    // =========================
    // 유저 게시글 (커서 없음)
    // =========================
    @Query("""
        select p from Post p
        where p.author.id = :userId
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findUserPostsFirstPage(
            @Param("userId") Long userId,
            Pageable pageable
    );

    // =========================
    // 유저 게시글 (커서 있음)
    // =========================
    @Query("""
        select p from Post p
        where p.author.id = :userId
          and (
            p.createdAt < :cursorTime
            or (p.createdAt = :cursorTime and p.id < :cursorId)
          )
        order by p.createdAt desc, p.id desc
    """)
    List<Post> findUserPostsNextPage(
            @Param("userId") Long userId,
            @Param("cursorTime") Instant cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
