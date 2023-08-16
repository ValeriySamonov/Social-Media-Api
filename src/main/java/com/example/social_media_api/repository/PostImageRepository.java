package com.example.social_media_api.repository;

import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
}
