package com.example.social_media_api.dto.message;

import lombok.Data;

@Data
public class MessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
}
