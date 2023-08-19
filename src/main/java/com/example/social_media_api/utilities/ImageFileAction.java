package com.example.social_media_api.utilities;

import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.repository.PostImageRepository;
import com.example.social_media_api.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageFileAction {

    @Value("${upload.dir:/default/path}")
    private String uploadDir;

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

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

    public void saveFiles(List<MultipartFile> files, Post post) {
        List<PostImage> postImageList = files.stream()
                .map(this::createImage)
                .map(fileName -> new PostImage().setFileName(fileName))
                .map(postImage -> postImage.setPost(post))
                .collect(Collectors.toList()); // Собираем в список

        postImageRepository.saveAll(postImageList); // Пакетное сохранение всех объектов

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

            e.printStackTrace();
        }
    }

}
