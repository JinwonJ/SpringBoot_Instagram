package org.clonestudy.instagram.like.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.like.domain.PostLike;
import org.clonestudy.instagram.like.dto.LikeResponse;
import org.clonestudy.instagram.like.repo.PostLikeRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponse like(Long meId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!postLikeRepository.existsByPost_IdAndUser_Id(postId, meId)) {
            postLikeRepository.save(PostLike.builder().post(post).user(me).build());
        }

        long count = postLikeRepository.countByPost_Id(postId);
        return new LikeResponse(postId, count, true);
    }

    @Transactional
    public LikeResponse unlike(Long meId, Long postId) {
        // deleteBy... 방식 권장 (findBy 없어도 됨)
        postLikeRepository.deleteByPost_IdAndUser_Id(postId, meId);

        long count = postLikeRepository.countByPost_Id(postId);
        boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(postId, meId);
        return new LikeResponse(postId, count, liked);
    }

    @Transactional(readOnly = true)
    public LikeResponse status(Long meId, Long postId) {
        long count = postLikeRepository.countByPost_Id(postId);
        boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(postId, meId);
        return new LikeResponse(postId, count, liked);
    }
}
