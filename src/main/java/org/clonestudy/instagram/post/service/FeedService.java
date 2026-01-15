package org.clonestudy.instagram.post.service;

import org.clonestudy.instagram.follow.repo.FollowRepository;
import org.clonestudy.instagram.post.dto.FeedItemResponse;
import org.clonestudy.instagram.post.dto.FeedResponse;
import org.clonestudy.instagram.post.repo.PostRepository;
import lombok.RequiredArgsConstructor;
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

    // 홈 피드: 팔로잉 + 나
    @Transactional(readOnly = true)
    public FeedResponse getFeed(Long meId, String cursor, int size) {
        List<Long> followingIds = followRepository.findFollowingIds(meId);
        List<Long> authorIds = new ArrayList<>(followingIds);
        authorIds.add(meId);

        Cursor parsed = parseCursor(cursor);

        var pageable = PageRequest.of(0, clampSize(size));
        var posts = postRepository.findFeedPage(authorIds, parsed.cursorTime, parsed.cursorId, pageable);

        var items = posts.stream()
                .map(p -> new FeedItemResponse(
                        p.getId(),
                        p.getAuthor().getId(),
                        p.getAuthor().getUsername(),
                        p.getCaption(),
                        p.getImages().stream().map(i -> i.getImageUrl()).toList(),
                        p.getCreatedAt()
                ))
                .toList();

        String nextCursor = makeNextCursor(posts, pageable.getPageSize());
        return new FeedResponse(items, nextCursor);
    }

    // ✅ 유저(프로필) 피드: 특정 유저의 게시글만
    @Transactional(readOnly = true)
    public FeedResponse getUserFeed(Long userId, String cursor, int size) {
        Cursor parsed = parseCursor(cursor);

        var pageable = PageRequest.of(0, clampSize(size));
        var posts = postRepository.findUserPostsPage(userId, parsed.cursorTime, parsed.cursorId, pageable);

        var items = posts.stream()
                .map(p -> new FeedItemResponse(
                        p.getId(),
                        p.getAuthor().getId(),
                        p.getAuthor().getUsername(),
                        p.getCaption(),
                        p.getImages().stream().map(i -> i.getImageUrl()).toList(),
                        p.getCreatedAt()
                ))
                .toList();

        String nextCursor = makeNextCursor(posts, pageable.getPageSize());
        return new FeedResponse(items, nextCursor);
    }

    private int clampSize(int size) {
        return Math.min(Math.max(size, 1), 50);
    }

    private String makeNextCursor(List<?> posts, int pageSize) {
        if (posts == null || posts.isEmpty()) return null;
        if (posts.size() < pageSize) return null;

        // posts는 Post 리스트지만 제네릭 처리 없이 안전하게 캐스팅
        var last = (org.clonestudy.instagram.post.domain.Post) posts.get(posts.size() - 1);
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


//package org.clonestudy.instagram.post.service;
//
//import org.clonestudy.instagram.follow.repo.FollowRepository;
//import org.clonestudy.instagram.post.dto.*;
//import org.clonestudy.instagram.post.repo.PostRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class FeedService {
//
//    private final FollowRepository followRepository;
//    private final PostRepository postRepository;
//
//    @Transactional(readOnly = true)
//    public FeedResponse getFeed(Long meId, String cursor, int size) {
//        List<Long> followingIds = followRepository.findFollowingIds(meId);
//        List<Long> authorIds = new ArrayList<>(followingIds);
//        authorIds.add(meId);
//
//        Instant cursorTime = null;
//        Long cursorId = null;
//
//        if (cursor != null && !cursor.isBlank()) {
//            String[] parts = cursor.split(":");
//            cursorTime = Instant.ofEpochMilli(Long.parseLong(parts[0]));
//            cursorId = Long.parseLong(parts[1]);
//        }
//
//        var pageable = PageRequest.of(0, Math.min(Math.max(size, 1), 50));
//        var posts = postRepository.findFeedPage(authorIds, cursorTime, cursorId, pageable);
//
//        var items = posts.stream()
//                .map(p -> new FeedItemResponse(
//                        p.getId(),
//                        p.getAuthor().getId(),
//                        p.getAuthor().getUsername(),
//                        p.getCaption(),
//                        p.getImages().stream().map(i -> i.getImageUrl()).toList(),
//                        p.getCreatedAt()
//                ))
//                .toList();
//
//        String nextCursor = null;
//        if (!posts.isEmpty() && posts.size() == pageable.getPageSize()) {
//            var last = posts.get(posts.size() - 1);
//            nextCursor = last.getCreatedAt().toEpochMilli() + ":" + last.getId();
//        }
//
//        return new FeedResponse(items, nextCursor);
//    }
//}
