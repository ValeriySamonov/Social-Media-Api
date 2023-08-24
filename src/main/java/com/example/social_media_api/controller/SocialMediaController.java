package com.example.social_media_api.controller;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.jwt.JwtAuthentication;
import com.example.social_media_api.repository.UserRepository;
import com.example.social_media_api.service.social.SocialMediaService;
import com.example.social_media_api.service.user.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    private final AuthService authService;
    private final UserRepository userRepository;

    // Создание поста
    @Operation(summary = "Создать пост", description = "Пользователи могут создавать новые посты, указывая текст, заголовок и прикрепляя изображения.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно опубликован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    public Long createPost(
            @ModelAttribute("createPostDTO") CreatePostDTO createPostDTO,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        createPostDTO.setCreatorId(userId);

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
            @Parameter(description = "ID поста для редактирования") @PathVariable Long postId,
            @ModelAttribute UpdatePostDTO updatePostDTO,
            @RequestParam(name = "addedFiles", required = false) List<MultipartFile> addedFiles) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        updatePostDTO.setUserId(userId);

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
            @Parameter(description = "ID поста для удаления") @RequestParam Long postId) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

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
            @RequestBody FriendshipDTO friendshipDTO) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        friendshipDTO.setUserId(userId);

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
            @RequestBody FriendshipDTO friendshipDTO) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        friendshipDTO.setUserId(userId);

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
            @RequestBody FriendshipDTO friendshipDTO) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        friendshipDTO.setUserId(userId);

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
            @RequestBody FriendshipDTO friendshipDTO) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        friendshipDTO.setUserId(userId);

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
            @Parameter(description = "Номер страницы") @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        JwtAuthentication authInfo = authService.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();

        Page<PostDTO> activityFeed = socialMediaService.getUserActivityFeed(userId, page);
        return ResponseEntity.ok(activityFeed);
    }


}
