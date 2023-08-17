package com.example.social_media_api.controller;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.service.SocialMediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final SocialMediaService socialMediaService;

    public MessageController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("content") String content) {
        MessageDTO messageDTO = socialMediaService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/chat")
    public ResponseEntity<List<MessageDTO>> getChat(
            @RequestParam("user1Id") Long user1Id,
            @RequestParam("user2Id") Long user2Id) {
        List<MessageDTO> chat = socialMediaService.getChat(user1Id, user2Id);
        return ResponseEntity.ok(chat);
    }
}

