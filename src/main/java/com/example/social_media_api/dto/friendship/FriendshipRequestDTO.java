package com.example.social_media_api.dto.friendship;

import lombok.Data;

@Data
public class FriendshipRequestDTO {
    private Long userId;
    private Long targetUserId;
}
