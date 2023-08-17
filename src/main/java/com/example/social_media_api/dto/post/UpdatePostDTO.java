package com.example.social_media_api.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePostDTO {
    private Long userId;
    private String title;
    private String text;
    private List<Long> removedFileIds;

}