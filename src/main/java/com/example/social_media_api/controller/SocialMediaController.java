package com.example.social_media_api.controller;

import com.example.social_media_api.dto.*;
import com.example.social_media_api.model.UserActivity;
import com.example.social_media_api.service.SocialMediaService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    public SocialMediaController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }


    // Создание пользователя
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDTO createUserDTO) {
        socialMediaService.createUser(createUserDTO);
        return ResponseEntity.ok("Пользователь успешно создан");
    }

    // Создание поста
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public ResponseEntity<String> createPost(
            @RequestBody @Schema CreatePostDTO createPostDTO,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        socialMediaService.createPost(createPostDTO, files);
        return ResponseEntity.ok("Пост успешно опубликован");
    }

    // Получение поста по ID
    @GetMapping("/posts/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostByUserId(@PathVariable Long userId,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        return ResponseEntity.ok(socialMediaService.getPostByUserId(userId, page));
    }

    // Отправка запроса в друзья
    @PostMapping("/friendships")
    public void sendFriendRequest(@RequestBody FriendshipDTO friendshipDTO) {
        socialMediaService.sendFriendRequest(friendshipDTO);
    }

    // Принятие запроса в друзья
    @PutMapping("/friendships/accept")
    public void acceptFriendRequest(@RequestBody FriendshipDTO friendshipDTO) {
/*        Friendship existingFriendship = friendshipRepository.findById(friendshipDTO.getId()).orElse(null);
        if (existingFriendship != null) {
            existingFriendship.setStatus("accepted");
            return friendshipRepository.save(existingFriendship);
        }*/
        socialMediaService.acceptFriendRequest(friendshipDTO);
    }

    // Отклонение запроса в друзья
    @PutMapping("/friendships/decline")
    public void declineFriendRequest(@RequestBody FriendshipDTO friendshipDTO) {
/*        Friendship existingFriendship = friendshipRepository.findById(friendshipDTO.getId()).orElse(null);
        if (existingFriendship != null) {
            existingFriendship.setStatus("declined");
            return friendshipRepository.save(existingFriendship);
        }*/
        socialMediaService.declineFriendRequest(friendshipDTO);
    }

    // Получение ленты активности пользователя
    @GetMapping("/feed/{userId}")
    public List<UserActivity> getUserFeed(@PathVariable Long userId) {
        /*        return userActivityRepository.findByUserIdOrderByCreatedAtDesc(userId);*/
        return socialMediaService.getUserFeed(userId);
    }

    // Обновление поста
    @PutMapping("/posts/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestBody PostDTO updatedPost) {
/*        Post existingPost = postRepository.findById(postId).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setText(updatedPost.getText());
            existingPost.setImageUrl(updatedPost.getImageUrl());
            return postRepository.save(existingPost);
        }*/
        socialMediaService.updatePost(postId, updatedPost);
    }

    // Удаление поста
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId) {
        socialMediaService.deletePost(postId);
    }

    // Добавление записи активности пользователя
    public void addUserActivity(UserActivityDTO activity) {
        socialMediaService.addUserActivity(activity);
    }

}
