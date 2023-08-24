package com.example.social_media_api.controller;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.jwt.JwtAuthentication;
import com.example.social_media_api.repository.UserRepository;
import com.example.social_media_api.service.message.MessageService;
import com.example.social_media_api.service.auth.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Message", description = "Отправка сообщений друзьям")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AuthServiceImpl authServiceImpl;
    private final UserRepository userRepository;

    @Operation(summary = "Отправить сообщение", description = "Друзья могут писать друг другу сообщения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение успешно отправлено"),
            @ApiResponse(responseCode = "400", description = "Вы не можете подписаться на себя/писать себе"),
            @ApiResponse(responseCode = "409", description = "Пользователь не являются вашим другом")
    })
    @PostMapping
    public void sendMessage(
            @RequestBody MessageDTO messageDTO) {

        JwtAuthentication authInfo = authServiceImpl.getAuthInfo();
        Long userId = userRepository.findByUsername((String) authInfo.getPrincipal()).orElseThrow(UserNotFoundException::new).getId();
        messageDTO.setSenderId(userId);

        messageService.sendMessage(messageDTO);
    }

}

