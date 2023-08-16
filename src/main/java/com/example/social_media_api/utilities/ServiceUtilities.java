package com.example.social_media_api.utilities;

import com.example.social_media_api.dto.PostDTO;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.repository.PostImageRepository;
import com.example.social_media_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class ServiceUtilities {

    @Value("${upload.dir:/default/path}")
    private String uploadDir;

    public void saveFiles(List<MultipartFile> files, Post post, PostRepository postRepository, PostImageRepository postImageRepository) {

        // Проверка и создание директории, если не существует
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (MultipartFile file : files) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            try {
                Files.copy(file.getInputStream(), filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            PostImage postImage = new PostImage();
            postImage.setFileName(fileName);
            postImage.setPost(post);
            postImageRepository.save(postImage);

        }

        post.setImages(postImageRepository.findByPost(post));
        postRepository.save(post);
    }

    public void deleteImageFromDirectory(String imageUrl) {

        Path imagePath = Paths.get(uploadDir).resolve(imageUrl);

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            // Обработка ошибки удаления файла
            e.printStackTrace();
        }
    }

    public PostDTO mapToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        //postDTO.setId(post.getId());
        postDTO.setUserId(post.getUser().getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setText(post.getText());
        postDTO.setCreatedAt(post.getCreatedAt());

        List<String> imageUrls = post.getImages().stream()
                .map(image -> uploadDir + image.getFileName()) // Путь к изображению
                .collect(Collectors.toList());

        postDTO.setImageUrls(imageUrls);

        return postDTO;
    }
}
