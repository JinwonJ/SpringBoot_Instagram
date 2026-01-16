package org.clonestudy.instagram.post.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.comment.repo.CommentCountProjection;
import org.clonestudy.instagram.comment.repo.CommentRepository;
import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.like.repo.LikeCountProjection;
import org.clonestudy.instagram.like.repo.PostLikeRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.dto.FeedItemResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    // 홈 피드: 팔로잉 + 나
    @Transactional(readOnly = true)
    public FeedResponse getFeed(Long meId, String cursor, int size) {
        List<Long> followingIds = followRepository.findFollowingIds(meId);
        List<Long> authorIds = new ArrayList<>(followingIds);
        authorIds.add(meId);

        Cursor parsed = parseCursor(cursor);
        var pageable = PageRequest.of(0, clampSize(size));

        List<Post> posts;
        if (parsed.cursorTime == null || parsed.cursorId == null) {
            posts = postRepository.findFeedFirstPage(authorIds, pageable);
        } else {
            posts = postRepository.findFeedNextPage(authorIds, parsed.cursorTime, parsed.cursorId, pageable);
        }

        return buildFeedResponse(meId, posts, pageable.getPageSize());
    }

    // 프로필 피드: 특정 유저 게시글만
    @Transactional(readOnly = true)
    public FeedResponse getUserFeed(Long meId, Long userId, String cursor, int size) {
        Cursor parsed = parseCursor(cursor);
        var pageable = PageRequest.of(0, clampSize(size));

        List<Post> posts;
        if (parsed.cursorTime == null || parsed.cursorId == null) {
            posts = postRepository.findUserPostsFirstPage(userId, pageable);
        } else {
            posts = postRepository.findUserPostsNextPage(userId, parsed.cursorTime, parsed.cursorId, pageable);
        }

        return buildFeedResponse(meId, posts, pageable.getPageSize());
    }

    private FeedResponse buildFeedResponse(Long meId, List<Post> posts, int pageSize) {
        if (posts == null || posts.isEmpty()) {
            return new FeedResponse(List.of(), null);
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // ✅ 좋아요 count 한 번에
        Map<Long, Long> likeCountMap = postLikeRepository.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(LikeCountProjection::getPostId, LikeCountProjection::getCnt));

        // ✅ 댓글 count 한 번에
        Map<Long, Long> commentCountMap = commentRepository.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(CommentCountProjection::getPostId, CommentCountProjection::getCnt));

        // ✅ 내가 좋아요한 게시글 id 한 번에
        Set<Long> likedPostIds = new HashSet<>(postLikeRepository.findLikedPostIds(meId, postIds));

        List<FeedItemResponse> items = posts.stream()
                .map(p -> {
                    long likeCount = likeCountMap.getOrDefault(p.getId(), 0L);
                    long commentCount = commentCountMap.getOrDefault(p.getId(), 0L);
                    boolean liked = likedPostIds.contains(p.getId());

                    return new FeedItemResponse(
                            p.getId(),
                            p.getAuthor().getId(),
                            p.getAuthor().getUsername(),
                            p.getCaption(),
                            p.getImages().stream().map(i -> i.getImageUrl()).toList(),
                            p.getCreatedAt(),
                            liked,
                            likeCount,
                            commentCount
                    );
                })
                .toList();

        String nextCursor = makeNextCursor(posts, pageSize);
        return new FeedResponse(items, nextCursor);
    }

    private int clampSize(int size) {
        return Math.min(Math.max(size, 1), 50);
    }

    private String makeNextCursor(List<Post> posts, int pageSize) {
        if (posts == null || posts.isEmpty()) return null;
        if (posts.size() < pageSize) return null;

        Post last = posts.get(posts.size() - 1);
        return last.getCreatedAt().toEpochMilli() + ":" + last.getId();
    }

    private Cursor parseCursor(String cursor) {
        Instant cursorTime = null;
        Long cursorId = null;

        if (cursor != null && !cursor.isBlank()) {
            String[] parts = cursor.split(":");
            cursorTime = Instant.ofEpochMilli(Long.parseLong(parts[0]));
            cursorId = Long.parseLong(parts[1]);
        }
        return new Cursor(cursorTime, cursorId);
    }

    private record Cursor(Instant cursorTime, Long cursorId) {}
}
