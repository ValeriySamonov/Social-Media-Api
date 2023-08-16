package com.example.social_media_api.service;

import com.example.social_media_api.dto.CreatePostDTO;
import com.example.social_media_api.dto.CreateUserDTO;
import com.example.social_media_api.dto.PostDTO;
import com.example.social_media_api.dto.UpdatePostDTO;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.*;
import com.example.social_media_api.utilities.ServiceUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    private static final int PAGE_SIZE = 10;

    private final ServiceUtilities serviceUtilities;

    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final UserActivityRepository userActivityRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public SocialMediaServiceImpl(ServiceUtilities serviceUtilities, UserRepository userRepository, PostImageRepository postImageRepository,
                                  UserActivityRepository userActivityRepository,
                                  PostRepository postRepository, FriendshipRepository friendshipRepository,
                                  SubscriptionRepository subscriptionRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.serviceUtilities = serviceUtilities;
        this.userRepository = userRepository;
        this.postImageRepository = postImageRepository;
        this.userActivityRepository = userActivityRepository;
        this.postRepository = postRepository;
        this.friendshipRepository = friendshipRepository;
        this.subscriptionRepository = subscriptionRepository;
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
        User user = userRepository.findById(1L).orElseThrow();
        Post post = modelMapper.map(createPostDTO, Post.class);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            serviceUtilities.saveFiles(files, post, postRepository, postImageRepository);
        }

    }

    @Override
    public Page<PostDTO> getPostByUserId(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Post> postsPage = postRepository.findByUser(user, pageable);

        return postsPage.map(serviceUtilities::mapToPostDTO);

    }

    @Override
    public void sendSubscriptionRequest(Long targetUserId) {

    }

    @Override
    public void acceptSubscriptionRequest(Long subscriberId) {

    }

    @Override
    public void declineSubscriptionRequest(Long subscriberId) {

    }

    @Override
    public void removeFriend(Long friendId) {

    }


    @Override
    public void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) {
        User user = userRepository.findById(1L).orElseThrow();
        Post post = postRepository.findByUserAndId(user, postId);

        System.out.println("Added files" + addedFiles);

        post.setTitle(updatePostDTO.getTitle());
        post.setText(updatePostDTO.getText());

        System.out.println("Files for deleting" + updatePostDTO.getRemovedFileIds());

        if (addedFiles != null && !addedFiles.isEmpty()) {

            serviceUtilities.saveFiles(addedFiles, post, postRepository, postImageRepository);
        }

        if (updatePostDTO.getRemovedFileIds() != null && !updatePostDTO.getRemovedFileIds().isEmpty()) {
            List<PostImage> removedImages = post.getImages().stream()
                    .filter(image -> updatePostDTO.getRemovedFileIds().contains(image.getId()))
                    .collect(Collectors.toList());

            System.out.println(removedImages);

            post.getImages().removeAll(removedImages);
            postImageRepository.deleteAll(removedImages);

            for (PostImage removedImage : removedImages) {
                serviceUtilities.deleteImageFromDirectory(removedImage.getFileName());
            }
        }

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        User user = userRepository.findById(1L).orElseThrow();
        Post post = postRepository.findByUserAndId(user, postId);
        postRepository.delete(post);
    }



}
