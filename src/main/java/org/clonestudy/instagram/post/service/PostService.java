package org.clonestudy.instagram.post.service;


import org.clonestudy.instagram.global.common.FileStorageService;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.dto.*;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public PostCreateResponse create(Long userId, String caption, List<MultipartFile> images) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("이미지 1개 이상 필요합니다.");
        }

        Post post = Post.builder()
                .author(author)
                .caption(caption)
                .build();

        for (MultipartFile img : images) {
            String url = fileStorageService.saveImage(img);
            post.addImage(url);
        }

        Post saved = postRepository.save(post);

        List<String> urls = saved.getImages().stream().map(i -> i.getImageUrl()).toList();

        return new PostCreateResponse(
                saved.getId(),
                author.getId(),
                author.getUsername(),
                saved.getCaption(),
                urls,
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponse get(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return new PostDetailResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCaption(),
                post.getImages().stream().map(i -> i.getImageUrl()).toList(),
                post.getCreatedAt()
        );
    }
}