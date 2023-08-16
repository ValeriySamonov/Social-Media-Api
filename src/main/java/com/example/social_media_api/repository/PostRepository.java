package com.example.social_media_api.repository;

import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageable);
}
