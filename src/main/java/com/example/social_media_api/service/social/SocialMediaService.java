package com.example.social_media_api.service.social;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SocialMediaService {

    Long createPost(Authentication authentication, CreatePostDTO createPostDTO, List<MultipartFile> files);
    Page<PostDTO> getPostByUserId(Authentication authentication, Long userId, int page);
    void sendFriendshipRequest(Authentication authentication, FriendshipDTO friendshipDTO);
    void acceptFriendshipRequest(Authentication authentication, FriendshipDTO friendshipDTO);
    void declineFriendshipRequest(Authentication authentication, FriendshipDTO friendshipDTO);
    void removeFriend(Authentication authentication, FriendshipDTO friendshipDTO);
    void updatePost(Authentication authentication, Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) throws IOException;
    void deletePost(Authentication authentication, Long userId, Long postId);
    Page<PostDTO> getUserActivityFeed(Authentication authentication, Long userId, int page);
    //User getByLogin(String login);

}
