package com.example.social_media_api.utilities.mapper;

import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MapEntityToDTO {

    private final ModelMapper modelMapper;

    public PostDTO mapToPostDTO(Post post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        postDTO.setUserId(post.getUser().getId());
        postDTO.setUsername(post.getUser().getUsername());

        // Маппинг поля fileUrls
        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getFileName)
                .toList();

        postDTO.setImageUrls(imageUrls);

        return postDTO;
    }
}
