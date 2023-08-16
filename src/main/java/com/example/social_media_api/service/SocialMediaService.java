package com.example.social_media_api.service;

import com.example.social_media_api.dto.CreatePostDTO;
import com.example.social_media_api.dto.CreateUserDTO;
import com.example.social_media_api.dto.PostDTO;
import com.example.social_media_api.dto.UpdatePostDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SocialMediaService {
    void createUser(CreateUserDTO createUserDTO);

    void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files);
    Page<PostDTO> getPostByUserId(Long userId, int page);
    void sendSubscriptionRequest(Long targetUserId);
    void acceptSubscriptionRequest(Long subscriberId);
    void declineSubscriptionRequest(Long subscriberId);
    void removeFriend(Long friendId);
    void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) throws IOException;
    void deletePost(Long postId);

}
