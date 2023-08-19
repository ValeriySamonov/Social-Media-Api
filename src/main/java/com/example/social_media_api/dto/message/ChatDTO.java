package com.example.social_media_api.dto.message;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatDTO {
    private Long user1Id;
    private Long user2Id;
}
