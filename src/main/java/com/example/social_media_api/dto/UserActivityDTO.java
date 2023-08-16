package com.example.social_media_api.dto;

import lombok.Data;

@Data
public class UserActivityDTO {
/*    private Long id;*/
    private Long userId;
    private String activityType;
    private Long activityId;

}
