package com.example.social_media_api.service.social;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SocialMediaService {

    Long createPost(CreatePostDTO createPostDTO, List<MultipartFile> files);
    Page<PostDTO> getPostByUserId(Long userId, int page);
    void sendFriendshipRequest(FriendshipDTO friendshipDTO);
    void acceptFriendshipRequest(FriendshipDTO friendshipDTO);
    void declineFriendshipRequest(FriendshipDTO friendshipDTO);
    void removeFriend(FriendshipDTO friendshipDTO);
    void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) throws IOException;
    void deletePost(Long userId, Long postId);
    Page<PostDTO> getUserActivityFeed(Long userId, int page);

}
