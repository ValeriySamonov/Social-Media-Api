package com.example.social_media_api.dto;

import lombok.Data;

@Data
public class FriendshipDTO {
/*    private Long id;*/
    private Long userId;
    private Long friendId;
    private String status;

}
