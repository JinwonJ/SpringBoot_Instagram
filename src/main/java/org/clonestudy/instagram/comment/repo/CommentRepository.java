package org.clonestudy.instagram.comment.repo;

import org.clonestudy.instagram.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 단일 게시글 댓글 목록
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 단일 게시글 댓글 개수
    long countByPostId(Long postId);

    // ✅ 피드 성능 개선: 여러 게시글 댓글 수를 한 번에
    @Query("""
        select c.post.id as postId, count(c.id) as cnt
        from Comment c
        where c.post.id in :postIds
        group by c.post.id
    """)
    List<CommentCountProjection> countByPostIds(@Param("postIds") List<Long> postIds);
}
