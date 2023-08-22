package com.example.social_media_api.service.message;

import com.example.social_media_api.dto.message.MessageDTO;

public interface MessageService {
    void sendMessage(MessageDTO messageDTO);
}
