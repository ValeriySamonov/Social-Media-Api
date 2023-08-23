package com.example.social_media_api.controller;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.security.SecurityUserPrincipal;
import com.example.social_media_api.service.social.SocialMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Social Media", description = "Основные операции API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SocialMediaController {

    private final SocialMediaService socialMediaService;

    // Создание поста
    @Operation(summary = "Создать пост", description = "Пользователи могут создавать новые посты, указывая текст, заголовок и прикрепляя изображения.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно опубликован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public Long createPost(
            Authentication authentication,
            @ModelAttribute("createPostDTO") CreatePostDTO createPostDTO,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        createPostDTO.setCreatorId(securityUserPrincipal.getUser().getId());

        return socialMediaService.createPost(createPostDTO, files);
    }

    // Получение всех постов пользователя по его ID
    @Operation(summary = "Получить пост пользователя", description = "Пользователи могут просматривать посты других пользователей.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно выполнен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @GetMapping("/posts/{postOwnerId}")
    public ResponseEntity<Page<PostDTO>> getPostByUserId(@Parameter(description = "ID автора поста") @PathVariable Long postOwnerId,
                                                         @Parameter(description = "Номер страницы") @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        return ResponseEntity.ok(socialMediaService.getPostByUserId(postOwnerId, page));
    }

    // Обновление поста
    @Operation(summary = "Редактировать свой пост", description = "Пользователи могут обновлять свои собственные посты.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Пользователь/пост не существует")
    })
    @PutMapping(value = "/posts/{postId}", consumes = "multipart/form-data")
    public void updatePost(
            Authentication authentication,
            @Parameter(description = "ID поста для редактирования") @PathVariable Long postId,
            @ModelAttribute UpdatePostDTO updatePostDTO,
            @RequestParam(name = "addedFiles", required = false) List<MultipartFile> addedFiles) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        updatePostDTO.setUserId(securityUserPrincipal.getUser().getId());

        try {
            socialMediaService.updatePost(postId, updatePostDTO, addedFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Удаление поста
    @Operation(summary = "Удалить свой пост", description = "Пользователи могут удалять свои собственные посты.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь/пост не существует")
    })
    @DeleteMapping("/posts")
    public void deletePost(
            Authentication authentication,
            @Parameter(description = "ID поста для удаления") @RequestParam Long postId) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        Long userId = securityUserPrincipal.getUser().getId();

        socialMediaService.deletePost(userId, postId);
    }


    // Отправка запроса на подписку (добавление в друзья)
    @Operation(summary = "Отправить запрос на дружбу (подписку)", description = "Пользователи могут отправлять заявки в друзья другим пользователям. " +
            "Пользователь, отправивший заявку, остается подписчиком до тех пор, пока сам не откажется от подписки.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на дружбу отправлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping("/friendship/request")
    public void sendFriendshipRequest(
            Authentication authentication,
            @RequestBody FriendshipDTO friendshipDTO) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        friendshipDTO.setUserId(securityUserPrincipal.getUser().getId());

        socialMediaService.sendFriendshipRequest(friendshipDTO);
    }

    // Принятие запроса на подписку (подтверждение дружбы)
    @Operation(summary = "Согласие на дружбу (принятие запроса)", description = "Если пользователь, получивший заявку, принимает ее, " +
            "оба пользователя становятся друзьями.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на дружбу принят"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping("/friendship/accept")
    public void acceptFriendshipRequest(
            Authentication authentication,
            @RequestBody FriendshipDTO friendshipDTO) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        friendshipDTO.setUserId(securityUserPrincipal.getUser().getId());

        socialMediaService.acceptFriendshipRequest(friendshipDTO);
    }

    // Отклонение запроса на подписку/Удаление друга (отклонение дружбы/отписка)
    @Operation(summary = "Несогласие на дружбу (отклонение запроса)", description = "Если пользователь, получивший заявку, отклоняет её, " +
            "то пользователь, отправивший заявку, все равно остается подписчиком.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос на дружбу отклонен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping("/friendship/reject")
    public void declineFriendshipRequest(
            Authentication authentication,
            @RequestBody FriendshipDTO friendshipDTO) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        friendshipDTO.setUserId(securityUserPrincipal.getUser().getId());

        socialMediaService.declineFriendshipRequest(friendshipDTO);
    }

    // Удаление друга (отписка)
    @Operation(summary = "Отказ от дружбы (подписки)", description = "Если один из друзей удаляет другого из друзей, то он также отписывается. " +
            "Второй пользователь при этом остаётся подписчиком первого.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вы больше не друзья/подписка отменена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping("/friendship/unfriend")
    public void removeFriend(
            Authentication authentication,
            @RequestBody FriendshipDTO friendshipDTO) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        friendshipDTO.setUserId(securityUserPrincipal.getUser().getId());

        socialMediaService.removeFriend(friendshipDTO);
    }

    //Лента активности
    @Operation(summary = "Лента активности пользователя", description = "Лента активности пользователя отображает последние посты от пользователей, " +
            "на которых он подписан.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос успешно выполнен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @GetMapping("/activity-feed")
    public ResponseEntity<Page<PostDTO>> getUserActivityFeed(
            Authentication authentication,
            @Parameter(description = "Номер страницы") @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        Long userId = securityUserPrincipal.getUser().getId();

        Page<PostDTO> activityFeed = socialMediaService.getUserActivityFeed(userId, page);
        return ResponseEntity.ok(activityFeed);
    }


}
