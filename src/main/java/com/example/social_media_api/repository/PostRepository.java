package com.example.social_media_api.repository;

import com.example.social_media_api.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByUserIdAndId(Long userId, Long postId);
    Page<Post> findByUserIdIn(List<Long> userIds, Pageable pageable);

    Page<Post> findByUserId(Long id, Pageable pageable);
}
