package com.example.social_media_api.service;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.dto.user.CreateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SocialMediaService {
    void createUser(CreateUserDTO createUserDTO);
    void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files);
    Page<PostDTO> getPostByUserId(Long userId, int page);
    void sendFriendshipRequest(FriendshipDTO friendshipDTO);
    void acceptFriendshipRequest(FriendshipDTO friendshipDTO);
    void declineFriendshipRequest(FriendshipDTO friendshipDTO);
    void removeFriend(FriendshipDTO friendshipDTO);
    void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) throws IOException;
    void deletePost(Long userId, Long postId);
    void sendMessage(MessageDTO messageDTO);
    Page<PostDTO> getUserActivityFeed(Long userId, int page);

}
