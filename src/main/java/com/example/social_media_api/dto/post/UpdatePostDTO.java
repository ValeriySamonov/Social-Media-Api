package com.example.social_media_api.dto.post;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UpdatePostDTO {
    private String title;
    private String text;
    private List<Long> removedFileIds;

}