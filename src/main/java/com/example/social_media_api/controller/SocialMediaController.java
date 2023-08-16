package com.example.social_media_api.controller;

import com.example.social_media_api.dto.*;
import com.example.social_media_api.model.UserActivity;
import com.example.social_media_api.service.SocialMediaService;
import io.swagger.v3.oas.annotations.media.Schema;
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


    // Создание пользователя
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDTO createUserDTO) {
        socialMediaService.createUser(createUserDTO);
        return ResponseEntity.ok("Пользователь успешно создан");
    }

    // Создание поста
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public ResponseEntity<String> createPost(
            @ModelAttribute @Schema CreatePostDTO createPostDTO,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        socialMediaService.createPost(createPostDTO, files);
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
    @PostMapping("/subscribe/{targetUserId}")
    public ResponseEntity<String> sendSubscriptionRequest(
            @PathVariable Long targetUserId) {
        socialMediaService.sendSubscriptionRequest(targetUserId);
        return ResponseEntity.ok("Запрос на подписку отправлен");
    }


    // Принятие запроса на подписку (подтверждение дружбы)
    @PostMapping("/subscribe/accept/{subscriberId}")
    public ResponseEntity<String> acceptSubscriptionRequest(
            @PathVariable Long subscriberId) {
        socialMediaService.acceptSubscriptionRequest(subscriberId);
        return ResponseEntity.ok("Запрос на подписку принят");
    }


    // Отклонение запроса на подписку (отклонение дружбы)
    @PostMapping("/subscribe/reject/{subscriberId}")
    public ResponseEntity<String> declineSubscriptionRequest(
            @PathVariable Long subscriberId) {
        socialMediaService.declineSubscriptionRequest(subscriberId);
        return ResponseEntity.ok("Запрос на подписку отклонен");
    }

    // Удаление друга (отписка)
    @DeleteMapping("/unsubscribe/{friendId}")
    public ResponseEntity<String> removeFriend(
            @PathVariable Long friendId) {
        socialMediaService.removeFriend(friendId);
        return ResponseEntity.ok("Вы больше не друзья");
    }

}
