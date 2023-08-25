package com.example.social_media_api.dto.post;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePostDTO {
    private String title;
    private String text;
}
