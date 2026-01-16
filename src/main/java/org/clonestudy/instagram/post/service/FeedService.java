package org.clonestudy.instagram.post.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.like.repo.PostLikeRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.dto.FeedItemResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

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

        List<FeedItemResponse> items = posts.stream()
                .map(p -> {
                    long likeCount = postLikeRepository.countByPost_Id(p.getId());
                    boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(p.getId(), meId);

                    return new FeedItemResponse(
                            p.getId(),
                            p.getAuthor().getId(),
                            p.getAuthor().getUsername(),
                            p.getCaption(),
                            p.getImages().stream().map(i -> i.getImageUrl()).toList(),
                            p.getCreatedAt(),
                            liked,
                            likeCount
                    );
                })
                .toList();

        String nextCursor = makeNextCursor(posts, pageable.getPageSize());
        return new FeedResponse(items, nextCursor);
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

        List<FeedItemResponse> items = posts.stream()
                .map(p -> {
                    long likeCount = postLikeRepository.countByPost_Id(p.getId());
                    boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(p.getId(), meId);

                    return new FeedItemResponse(
                            p.getId(),
                            p.getAuthor().getId(),
                            p.getAuthor().getUsername(),
                            p.getCaption(),
                            p.getImages().stream().map(i -> i.getImageUrl()).toList(),
                            p.getCreatedAt(),
                            liked,
                            likeCount
                    );
                })
                .toList();

        String nextCursor = makeNextCursor(posts, pageable.getPageSize());
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
