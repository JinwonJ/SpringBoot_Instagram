package org.clonestudy.instagram.comment.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.comment.domain.Comment;
import org.clonestudy.instagram.comment.dto.CommentCreateRequest;
import org.clonestudy.instagram.comment.dto.CommentResponse;
import org.clonestudy.instagram.comment.dto.CommentUpdateRequest;
import org.clonestudy.instagram.comment.repo.CommentRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long userId, Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        comment.validateOwner(userId);
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse update(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 작성자만 수정 가능
        comment.validateOwner(userId);

        // 변경 적용
        comment.updateContent(request.getContent());

        // dirty checking으로 update됨 (save 호출 없어도 됨)
        return CommentResponse.from(comment);
    }
}
