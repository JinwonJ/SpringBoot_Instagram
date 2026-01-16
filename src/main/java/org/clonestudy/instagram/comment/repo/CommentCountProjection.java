package org.clonestudy.instagram.comment.repo;

public interface CommentCountProjection {
    Long getPostId();
    Long getCnt();
}
