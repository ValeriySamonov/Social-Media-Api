package com.example.social_media_api.utilities;

import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.PostImageRepository;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ServiceUtilities {

    @Value("${upload.dir:/default/path}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    public void saveFiles(List<MultipartFile> files, Post post) {

        List<PostImage> postImageList = files.stream()
                .map(this::createImage)
                .map(fileName -> new PostImage().setFileName(fileName))
                .map(postImage -> postImage.setPost(post))
                .map(postImageRepository::save)
                .toList();

        post.setImages(postImageList);
        postRepository.save(post);
    }

    private String createImage(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
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

    @PostConstruct
    public void enableDirectory() {

        // Проверка и создание директории, если не существует
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> checkUsersForMessaging(Long userId1, Long userId2) {

        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Нельзя отправлять сообщение самому себе.");
        }

        List<User> users = new ArrayList<>();
        User sender = userRepository.findById(userId1).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(userId2).orElseThrow(UserNotFoundException::new);
        users.add(0, sender);
        users.add(1, receiver);

        return users;
    }

}
