package org.clonestudy.instagram.post.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.global.error.InstaException;
import org.clonestudy.instagram.like.domain.PostLike;
import org.clonestudy.instagram.like.repo.PostLikeRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.dto.PostLikeResponse;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService_copy {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository likeRepository;

    @Transactional
    public PostLikeResponse like(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> InstaException.notFound("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> InstaException.notFound("User not found"));

        // 이미 좋아요면 그대로 반환
        if (!likeRepository.existsByPost_IdAndUser_Id(postId, userId)) {
            likeRepository.save(PostLike.builder().post(post).user(user).build());
        }

        long count = likeRepository.countByPost_Id(postId);
        return new PostLikeResponse(postId, true, count);
    }

    @Transactional
    public PostLikeResponse unlike(Long userId, Long postId) {
        // 존재하면 삭제 (없으면 그냥 통과)
        likeRepository.deleteByPost_IdAndUser_Id(postId, userId);

        long count = likeRepository.countByPost_Id(postId);
        boolean liked = likeRepository.existsByPost_IdAndUser_Id(postId, userId);

        return new PostLikeResponse(postId, liked, count); // liked는 보통 false가 됨
    }

    @Transactional
    public PostLikeResponse toggle(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> InstaException.notFound("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> InstaException.notFound("User not found"));

        boolean exists = likeRepository.existsByPost_IdAndUser_Id(postId, userId);
        if (exists) {
            likeRepository.deleteByPost_IdAndUser_Id(postId, userId);
        } else {
            likeRepository.save(PostLike.builder().post(post).user(user).build());
        }

        long count = likeRepository.countByPost_Id(postId);
        return new PostLikeResponse(postId, !exists, count);
    }

    @Transactional(readOnly = true)
    public PostLikeResponse status(Long userId, Long postId) {
        boolean liked = likeRepository.existsByPost_IdAndUser_Id(postId, userId);
        long count = likeRepository.countByPost_Id(postId);
        return new PostLikeResponse(postId, liked, count);
    }
}
