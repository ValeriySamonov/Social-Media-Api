package com.example.social_media_api.dto.post;

import lombok.Data;

@Data
public class CreatePostDTO {
    private String title;
    private String text;
}