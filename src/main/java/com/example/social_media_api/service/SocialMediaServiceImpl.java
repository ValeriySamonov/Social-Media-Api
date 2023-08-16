package com.example.social_media_api.service;

import com.example.social_media_api.dto.*;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.model.User;
import com.example.social_media_api.model.UserActivity;
import com.example.social_media_api.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    @Value("${upload.dir:/default/path}")
    private String uploadDir;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final UserActivityRepository userActivityRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public SocialMediaServiceImpl(UserRepository userRepository, PostImageRepository postImageRepository,
                                  UserActivityRepository userActivityRepository,
                                  PostRepository postRepository, FriendshipRepository friendshipRepository,
                                  ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.postImageRepository = postImageRepository;
        this.userActivityRepository = userActivityRepository;
        this.postRepository = postRepository;
        this.friendshipRepository = friendshipRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        User user = modelMapper.map(createUserDTO, User.class);
        userRepository.save(user);
    }

    @Override
    public void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files) {
        try {
            User user = userRepository.findById(1L).orElseThrow();
            Post post = modelMapper.map(createPostDTO, Post.class);
            post.setUser(user);
            post.setCreatedAt(LocalDateTime.now());
            postRepository.save(post);

            if (files != null && !files.isEmpty()) {

                for (MultipartFile file : files) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.copy(file.getInputStream(), filePath);

                    PostImage postImage = new PostImage();
                    postImage.setFileName(fileName);
                    postImage.setPost(post);
                    postImageRepository.save(postImage);

                }

                post.setImages(postImageRepository.findByPost(post));
                postRepository.save(post);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error uploading files", e);
        }
    }

    @Override
    public Page<PostDTO> getPostByUserId(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Post> postsPage = postRepository.findByUser(user, pageable);

        return postsPage.map(this::mapToPostDTO);

    }

    private PostDTO mapToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
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


    @Override
    public void sendFriendRequest(FriendshipDTO friendshipDTO) {

    }

    @Override
    public void acceptFriendRequest(FriendshipDTO friendshipDTO) {

    }

    @Override
    public void declineFriendRequest(FriendshipDTO friendshipDTO) {

    }

    @Override
    public List<UserActivity> getUserFeed(Long userId) {

        return null;
    }

    @Override
    public void updatePost(Long postId, PostDTO updatedPost) {

    }

    @Override
    public void deletePost(Long postId) {

    }

    @Override
    public void addUserActivity(UserActivityDTO activity) {

    }
}
