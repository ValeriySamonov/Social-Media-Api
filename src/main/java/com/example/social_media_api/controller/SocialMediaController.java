package com.example.social_media_api.controller;

import com.example.social_media_api.dto.friendship.ActionFriendship;
import com.example.social_media_api.dto.friendship.FriendshipRequestDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.DeletePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.service.SocialMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    // Создание поста
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public ResponseEntity<String> createPost(
            @ModelAttribute CreatePostDTO createPostDTO,
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
    @DeleteMapping("/posts")
    public ResponseEntity<String> deletePost(
            @RequestBody DeletePostDTO deletePostDTO) {
        socialMediaService.deletePost(deletePostDTO);
        return ResponseEntity.ok("Пост успешно удалён");
    }

    // Отправка запроса на подписку (добавление в друзья)
    @PostMapping("/friendship/request")
    public ResponseEntity<String> sendFriendshipRequest(
            @RequestBody FriendshipRequestDTO friendshipRequestDTO) {
        socialMediaService.sendFriendshipRequest(friendshipRequestDTO);
        return ResponseEntity.ok("Запрос на дружбу отправлен");
    }


    // Принятие запроса на подписку (подтверждение дружбы)
    @PostMapping("/friendship/accept")
    public ResponseEntity<String> acceptFriendshipRequest(
            @RequestBody ActionFriendship actionFriendship) {
        socialMediaService.acceptFriendshipRequest(actionFriendship);
        return ResponseEntity.ok("Запрос на дружбу принят");
    }


    // Отклонение запроса на подписку/Удаление друга (отклонение дружбы/отписка)
    @PostMapping("/friendship/reject")
    public ResponseEntity<String> declineFriendshipRequest(
            @RequestBody ActionFriendship actionFriendship) {
        socialMediaService.declineFriendshipRequest(actionFriendship);
        return ResponseEntity.ok("Запрос на дружбу отклонен");
    }

    // Удаление друга (отписка)
    @DeleteMapping("/friendship")
    public ResponseEntity<String> removeFriend(
            @RequestBody ActionFriendship actionFriendship) {
        socialMediaService.removeFriend(actionFriendship);
        return ResponseEntity.ok("Вы больше не друзья/подписка отменена");
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
