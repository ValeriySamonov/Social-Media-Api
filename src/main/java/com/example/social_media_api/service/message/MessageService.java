package com.example.social_media_api.service.message;

import com.example.social_media_api.dto.message.MessageDTO;
import org.springframework.security.core.Authentication;

public interface MessageService {
    void sendMessage(Authentication authentication, MessageDTO messageDTO);
}
