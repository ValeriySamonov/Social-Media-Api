package com.example.social_media_api.dto.post;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeletePostDTO {
    private Long userId;
    private Long postId;
}
