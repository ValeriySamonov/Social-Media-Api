package com.example.social_media_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String text;
    private LocalDateTime createdAt;
    private List<String> imageUrls;

}
