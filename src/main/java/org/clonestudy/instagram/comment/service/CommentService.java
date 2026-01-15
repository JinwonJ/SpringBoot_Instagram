package org.clonestudy.instagram.comment.service;

import org.clonestudy.instagram.comment.domain.Comment;
import org.clonestudy.instagram.comment.dto.*;
import org.clonestudy.instagram.comment.repo.CommentRepository;
import org.clonestudy.instagram.global.error.InstaException;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long userId, Long postId, CommentCreateRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> InstaException.notFound("Post not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> InstaException.notFound("User not found"));

        Comment saved = commentRepository.save(Comment.builder()
                .post(post)
                .author(author)
                .content(req.content())
                .build());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long postId) {
        return commentRepository.findByPost_IdOrderByCreatedAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CommentResponse update(Long userId, Long commentId, CommentUpdateRequest req) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> InstaException.notFound("Comment not found"));

        if (!c.getAuthor().getId().equals(userId)) {
            throw InstaException.forbidden("댓글 수정 권한이 없습니다.");
        }

        c.setContent(req.content());
        return toResponse(c);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> InstaException.notFound("Comment not found"));

        if (!c.getAuthor().getId().equals(userId)) {
            throw InstaException.forbidden("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(c);
    }

    private CommentResponse toResponse(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getPost().getId(),
                c.getAuthor().getId(),
                c.getAuthor().getUsername(),
                c.getContent(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}