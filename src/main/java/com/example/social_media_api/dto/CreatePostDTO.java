package com.example.social_media_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreatePostDTO {

    @Schema(description = "Заголовок поста", example = "Заголовок поста", required = true)
    private String title;

    @Schema(description = "Текст поста", example = "Текст поста.", required = true)
    private String text;

}
