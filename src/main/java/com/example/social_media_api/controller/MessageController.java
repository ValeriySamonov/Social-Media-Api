package com.example.social_media_api.controller;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.security.SecurityUserPrincipal;
import com.example.social_media_api.service.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Отправить сообщение", description = "Друзья могут писать друг другу сообщения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение успешно отправлено"),
            @ApiResponse(responseCode = "400", description = "Вы не можете подписаться на себя/писать себе"),
            @ApiResponse(responseCode = "409", description = "Пользователь не являются вашим другом")
    })
    @PostMapping
    public void sendMessage(Authentication authentication, @RequestBody MessageDTO messageDTO) {

        SecurityUserPrincipal securityUserPrincipal = (SecurityUserPrincipal) authentication.getPrincipal();
        messageDTO.setSenderId(securityUserPrincipal.getUser().getId());

        messageService.sendMessage(messageDTO);
    }

}

