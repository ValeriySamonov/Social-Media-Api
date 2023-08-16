package com.example.social_media_api.service;

import com.example.social_media_api.dto.*;
import com.example.social_media_api.model.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SocialMediaService {
    void createUser(CreateUserDTO createUserDTO);

    void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files);
    Page<PostDTO> getPostByUserId(Long userId, int page);
    void sendFriendRequest(FriendshipDTO friendshipDTO);
    void acceptFriendRequest(FriendshipDTO friendshipDTO);
    void declineFriendRequest(FriendshipDTO friendshipDTO);
    List<UserActivity> getUserFeed(Long userId);
    void updatePost(Long postId, PostDTO updatedPost);
    void deletePost(Long postId);
    void addUserActivity(UserActivityDTO activity);
}
