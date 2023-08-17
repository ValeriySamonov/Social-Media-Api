package com.example.social_media_api.service;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.enums.FriendshipStatus;
import com.example.social_media_api.enums.PageSize;
import com.example.social_media_api.model.*;
import com.example.social_media_api.repository.*;
import com.example.social_media_api.utilities.ServiceUtilities;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    private final ServiceUtilities serviceUtilities;

    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    public SocialMediaServiceImpl(ServiceUtilities serviceUtilities,
                                  UserRepository userRepository,
                                  PostImageRepository postImageRepository,
                                  PostRepository postRepository,
                                  FriendshipRepository friendshipRepository,
                                  SubscriptionRepository subscriptionRepository,
                                  MessageRepository messageRepository,
                                  ModelMapper modelMapper) {
        this.serviceUtilities = serviceUtilities;
        this.userRepository = userRepository;
        this.postImageRepository = postImageRepository;
        this.postRepository = postRepository;
        this.friendshipRepository = friendshipRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        User user = modelMapper.map(createUserDTO, User.class);
        userRepository.save(user);
    }

    @Override
    public void createPost(Long creatorId, CreatePostDTO createPostDTO, List<MultipartFile> files) {
        User user = userRepository.findById(creatorId).orElseThrow();
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
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        Pageable pageable = PageRequest.of(page, PageSize.PAGE_SIZE.getSize(), Sort.by("createdAt").descending());

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
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content) {

        List<User> users = serviceUtilities.checkUsersForMessaging(senderId, receiverId, userRepository);

        Message message = new Message();
        message.setSender(users.get(0));
        message.setReceiver(users.get(1));
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

        return modelMapper.map(message, MessageDTO.class);
    }

    @Override
    public List<MessageDTO> getChat(Long user1Id, Long user2Id) {

        List<User> users = serviceUtilities.checkUsersForMessaging(user1Id, user2Id, userRepository);

        List<Message> chatMessages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtDesc(users.get(0), users.get(1), users.get(0), users.get(1));

        return chatMessages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .toList();
    }

    @Override
    public Page<PostDTO> getUserActivityFeed(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PageSize.PAGE_SIZE.getSize(), Sort.by("createdAt").descending());

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);

        List<Long> targetUserIds = subscriptions.stream()
                .map(Subscription::getTargetUser)
                .map(User::getId)
                .toList();

        List<Post> activityFeedPosts = postRepository.findByUserIdIn(targetUserIds);

        List<PostDTO> postDTOList = activityFeedPosts.stream()
                .map(serviceUtilities::mapToPostDTO)
                .toList();

        return new PageImpl<>(postDTOList, pageable, activityFeedPosts.size());
    }

}



