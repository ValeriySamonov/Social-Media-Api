package com.example.social_media_api.dto.friendship;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FriendshipDTO {
    private Long targetUserId;
}
