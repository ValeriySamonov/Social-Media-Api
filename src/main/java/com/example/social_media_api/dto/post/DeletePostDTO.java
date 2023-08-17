package com.example.social_media_api.dto.post;

import lombok.Data;

@Data
public class DeletePostDTO {
    private Long userId;
    private Long postId;
}
