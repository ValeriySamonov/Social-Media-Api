package com.example.social_media_api.utilities;

import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.PostImageRepository;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ServiceUtilities {

    @Value("${upload.dir:/default/path}")
    private String uploadDir;

    private final ModelMapper modelMapper;

    public ServiceUtilities(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

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

    public List<User> checkUsersForMessaging(Long userId1, Long userId2, UserRepository userRepository) {

        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Нельзя отправлять сообщение самому себе.");
        }

        List<User> users = new ArrayList<>();
        User sender = userRepository.findById(userId1)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId1 + " не найден"));
        User receiver = userRepository.findById(userId2)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId2 + " не найден"));
        users.add(0, sender);
        users.add(1, receiver);

        return users;
    }

}
