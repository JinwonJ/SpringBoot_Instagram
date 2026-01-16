package org.clonestudy.instagram.like.repo;

import org.clonestudy.instagram.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // ✅ 기존
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    void deleteByPost_IdAndUser_Id(Long postId, Long userId);

    long countByPost_Id(Long postId);

    // ✅ 피드 성능 개선: 여러 게시글 좋아요 count 한 번에
    @Query("""
        select pl.post.id as postId, count(pl.id) as cnt
        from PostLike pl
        where pl.post.id in :postIds
        group by pl.post.id
    """)
    List<LikeCountProjection> countByPostIds(@Param("postIds") List<Long> postIds);

    // ✅ 피드 성능 개선: 내가 좋아요한 postId 목록 한 번에
    @Query("""
        select pl.post.id
        from PostLike pl
        where pl.user.id = :meId and pl.post.id in :postIds
    """)
    List<Long> findLikedPostIds(@Param("meId") Long meId, @Param("postIds") List<Long> postIds);
}
