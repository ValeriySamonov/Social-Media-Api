package com.example.social_media_api.dto.friendship;

import lombok.Data;

@Data
public class ActionFriendship {
    private Long userId;
    private Long targetUserId;
}
