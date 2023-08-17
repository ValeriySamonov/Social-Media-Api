package com.example.social_media_api.controller;

import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.service.SocialMediaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    public SocialMediaController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    // Создание поста
    @PostMapping(value = "/posts/{creatorId}", consumes = "multipart/form-data")
    public ResponseEntity<String> createPost(
            @PathVariable Long creatorId,
            @ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        socialMediaService.createPost(creatorId, createPostDTO, files);
        return ResponseEntity.ok("Пост успешно опубликован");
    }

    // Получение всех постов по ID пользователя
    @GetMapping("/posts/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostByUserId(@PathVariable Long userId,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        return ResponseEntity.ok(socialMediaService.getPostByUserId(userId, page));
    }

    // Обновление поста
    @PutMapping(value = "/posts/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @ModelAttribute UpdatePostDTO updatePostDTO,
            @RequestParam(name = "addedFiles", required = false) List<MultipartFile> addedFiles) {

        try {
            socialMediaService.updatePost(postId, updatePostDTO, addedFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Пост успешно обновлён");
    }

    // Удаление поста
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        socialMediaService.deletePost(postId);
        return ResponseEntity.ok("Пост успешно удалён");
    }

    // Отправка запроса на подписку (добавление в друзья)
    @PostMapping("/friendship/{targetUserId}")
    public ResponseEntity<String> sendFriendshipRequest(
            @PathVariable Long targetUserId) {
        socialMediaService.sendFriendshipRequest(targetUserId);
        return ResponseEntity.ok("Запрос на дружбу отправлен");
    }


    // Принятие запроса на подписку (подтверждение дружбы)
    @PostMapping("/friendship/accept/{subscriberId}")
    public ResponseEntity<String> acceptFriendshipRequest(
            @PathVariable Long subscriberId) {
        socialMediaService.acceptFriendshipRequest(subscriberId);
        return ResponseEntity.ok("Запрос на дружбу принят");
    }


    // Отклонение запроса на подписку (отклонение дружбы)
    @PostMapping("/friendship/reject/{subscriberId}")
    public ResponseEntity<String> declineFriendshipRequest(
            @PathVariable Long subscriberId) {
        socialMediaService.declineFriendshipRequest(subscriberId);
        return ResponseEntity.ok("Запрос на дружбу отклонен");
    }

    // Удаление друга (отписка)
    @DeleteMapping("/friendship/{friendId}")
    public ResponseEntity<String> removeFriend(
            @PathVariable Long friendId) {
        socialMediaService.removeFriend(friendId);
        return ResponseEntity.ok("Вы больше не друзья");
    }

    //Лента активности
    @GetMapping("/activity-feed/{userId}")
    public ResponseEntity<Page<PostDTO>> getUserActivityFeed(
            @PathVariable Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        Page<PostDTO> activityFeed = socialMediaService.getUserActivityFeed(userId, page);
        return ResponseEntity.ok(activityFeed);
    }


}
