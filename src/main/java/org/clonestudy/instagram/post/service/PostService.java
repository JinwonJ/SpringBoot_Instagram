package org.clonestudy.instagram.post.service;

import lombok.RequiredArgsConstructor;
import org.clonestudy.instagram.global.common.FileStorageService;
import org.clonestudy.instagram.like.repo.PostLikeRepository;
import org.clonestudy.instagram.post.domain.Post;
import org.clonestudy.instagram.post.domain.PostImage;
import org.clonestudy.instagram.post.dto.PostCreateResponse;
import org.clonestudy.instagram.post.dto.PostDetailResponse;
import org.clonestudy.instagram.post.repo.PostRepository;
import org.clonestudy.instagram.user.domain.User;
import org.clonestudy.instagram.user.repo.UserRepository;
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

    // ✅ 좋아요 계산용
    private final PostLikeRepository postLikeRepository;

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

        List<String> urls = saved.getImages().stream()
                .map(PostImage::getImageUrl)
                .toList();

        return new PostCreateResponse(
                saved.getId(),
                author.getId(),
                author.getUsername(),
                saved.getCaption(),
                urls,
                saved.getCreatedAt()
        );
    }

    /**
     * ✅ 컨트롤러에서 principal.userId()를 넘겨서 호출하는 상세 조회
     * PostDetailResponse가 8필드(좋아요 포함)인 버전에 맞춤
     */
    @Transactional(readOnly = true)
    public PostDetailResponse get(Long meId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        long likeCount = postLikeRepository.countByPost_Id(postId);
        boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(postId, meId);

        return new PostDetailResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCaption(),
                post.getImages().stream().map(PostImage::getImageUrl).toList(),
                post.getCreatedAt(),
                liked,
                likeCount
        );
    }

    /**
     * (옵션) 기존 시그니처 유지가 필요하면 남겨두기
     * 이 버전은 meId를 모르니 liked=false로 반환
     */
    @Transactional(readOnly = true)
    public PostDetailResponse get(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        long likeCount = postLikeRepository.countByPost_Id(postId);

        return new PostDetailResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCaption(),
                post.getImages().stream().map(PostImage::getImageUrl).toList(),
                post.getCreatedAt(),
                false,
                likeCount
        );
    }
}
