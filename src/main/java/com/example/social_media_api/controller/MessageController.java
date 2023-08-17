package com.example.social_media_api.controller;

import com.example.social_media_api.dto.message.ChatDTO;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.service.SocialMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SocialMediaService socialMediaService;

    @PostMapping
    public ResponseEntity<String> sendMessage(
            @RequestBody MessageDTO messageDTO) {
        socialMediaService.sendMessage(messageDTO);
        return ResponseEntity.ok("Сообщение успешно отправлено");
    }

    @GetMapping("/chat")
    public ResponseEntity<List<MessageDTO>> getChat(
            @RequestBody ChatDTO chatDTO) {
        List<MessageDTO> chat = socialMediaService.getChat(chatDTO);
        return ResponseEntity.ok(chat);
    }
}

