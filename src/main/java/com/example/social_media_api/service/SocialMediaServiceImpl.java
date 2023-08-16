package com.example.social_media_api.service;

import com.example.social_media_api.dto.*;
import com.example.social_media_api.enums.FriendshipStatus;
import com.example.social_media_api.model.*;
import com.example.social_media_api.repository.*;
import com.example.social_media_api.utilities.ServiceUtilities;
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

    public SocialMediaServiceImpl(ServiceUtilities serviceUtilities, UserRepository userRepository, PostImageRepository postImageRepository,
                                  UserActivityRepository userActivityRepository,
                                  PostRepository postRepository, FriendshipRepository friendshipRepository,
                                  SubscriptionRepository subscriptionRepository, ModelMapper modelMapper) {
        this.serviceUtilities = serviceUtilities;
        this.userRepository = userRepository;
        this.postImageRepository = postImageRepository;
        this.userActivityRepository = userActivityRepository;
        this.postRepository = postRepository;
        this.friendshipRepository = friendshipRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.modelMapper = modelMapper;
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
    public void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) {
        User user = userRepository.findById(1L).orElseThrow();
        Post post = postRepository.findByUserAndId(user, postId);

        post.setTitle(updatePostDTO.getTitle());
        post.setText(updatePostDTO.getText());

        if (addedFiles != null && !addedFiles.isEmpty()) {

            serviceUtilities.saveFiles(addedFiles, post, postRepository, postImageRepository);
        }

        if (updatePostDTO.getRemovedFileIds() != null && !updatePostDTO.getRemovedFileIds().isEmpty()) {
            List<PostImage> removedImages = post.getImages().stream()
                    .filter(image -> updatePostDTO.getRemovedFileIds().contains(image.getId()))
                    .toList();

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

        List<PostImage> removedImages = post.getImages();
        for (PostImage removedImage : removedImages) {
            serviceUtilities.deleteImageFromDirectory(removedImage.getFileName());
        }

        postRepository.delete(post);
    }

    @Override
    public void sendFriendshipRequest(Long targetUserId) {
        User subscriber = userRepository.findById(1L).orElseThrow(); // Получите текущего пользователя (подписчика)
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + targetUserId + " not found"));

        if (subscriber.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("Вы не можете подписаться на себя");
        }

        Subscription subscription = new Subscription();
        Friendship friendship = new Friendship();

        subscription.setSubscriber(subscriber);
        subscription.setTargetUser(targetUser);
        subscription.setCreatedAt(LocalDateTime.now());

        friendship.setUser(subscriber);
        friendship.setFriend(targetUser);
        friendship.setStatus(FriendshipStatus.PENDING); // Устанавливаем статус "pending"
        friendship.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
        friendshipRepository.save(friendship);
    }

    @Override
    public void acceptFriendshipRequest(Long subscriberId) {
        User user = userRepository.findById(2L).orElseThrow(); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(subscriberId, user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих подтверждения запросов на дружбу");
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setCreatedAt(LocalDateTime.now());

        User subscriber = userRepository.findById(subscriberId).orElseThrow();

        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setTargetUser(subscriber);
        subscription.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
        friendshipRepository.save(friendship);
    }

    @Override
    public void declineFriendshipRequest(Long subscriberId) {
        User user = userRepository.findById(2L).orElseThrow(); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(subscriberId, user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих непринятия запросов на дружбу");
        }
        friendshipRepository.delete(friendship);
    }

    @Override
    public void removeFriend(Long friendId) {
        User user = userRepository.findById(2L).orElseThrow(); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(friendId, user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих непринятия запросов на дружбу");
        } else if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalArgumentException("У вас нет такого друга");
        }
        subscriptionRepository.delete(subscriptionRepository.findBySubscriberIdAndTargetUserId(user.getId(), friendId));
        friendshipRepository.delete(friendship);
    }

    @Override
    public void sendMessage(Long recipientId, MessageDTO messageDTO) {

    }

}
