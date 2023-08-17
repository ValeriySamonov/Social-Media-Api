package com.example.social_media_api.service;

import com.example.social_media_api.dto.friendship.ActionFriendship;
import com.example.social_media_api.dto.friendship.FriendshipRequestDTO;
import com.example.social_media_api.dto.message.ChatDTO;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.DeletePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.enums.FriendshipStatus;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.model.*;
import com.example.social_media_api.repository.*;
import com.example.social_media_api.utilities.MapEntityToDTO;
import com.example.social_media_api.utilities.ServiceUtilities;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialMediaServiceImpl implements SocialMediaService {

    static final int PAGE_SIZE = 10;

    private final ServiceUtilities serviceUtilities;
    private final MapEntityToDTO mapEntityToDTO;

    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        User user = modelMapper.map(createUserDTO, User.class);
        userRepository.save(user);
    }

    @Override
    public void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files) {
        User user = userRepository.findById(createPostDTO.getCreatorId()).orElseThrow(UserNotFoundException::new);
        Post post = modelMapper.map(createPostDTO, Post.class);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        if (!CollectionUtils.isEmpty(files)) {
            serviceUtilities.saveFiles(files, post);
        }

        postRepository.save(post);

    }

    @Override
    public Page<PostDTO> getPostByUserId(Long userId, int page) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Post> postsPage = postRepository.findByUser(user, pageable);

        return postsPage.map(mapEntityToDTO::mapToPostDTO);

    }

    @Override
    public void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) {
        User user = userRepository.findById(updatePostDTO.getUserId()).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByUserAndId(user, postId);

        post.setTitle(updatePostDTO.getTitle());
        post.setText(updatePostDTO.getText());

        if (addedFiles != null && !addedFiles.isEmpty()) {

            serviceUtilities.saveFiles(addedFiles, post);
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
    public void deletePost(DeletePostDTO deletePostDTO) {
        User user = userRepository.findById(deletePostDTO.getUserId()).orElseThrow();
        Post post = postRepository.findByUserAndId(user, deletePostDTO.getPostId());

        List<PostImage> removedImages = post.getImages();
        for (PostImage removedImage : removedImages) {
            serviceUtilities.deleteImageFromDirectory(removedImage.getFileName());
        }

        postRepository.delete(post);
    }

    @Override
    public void sendFriendshipRequest(FriendshipRequestDTO friendshipRequestDTO) {
        User subscriber = userRepository.findById(friendshipRequestDTO.getUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        User targetUser = userRepository.findById(friendshipRequestDTO.getTargetUserId()).orElseThrow(UserNotFoundException::new);

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
    public void acceptFriendshipRequest(ActionFriendship actionFriendship) {
        User user = userRepository.findById(actionFriendship.getUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(actionFriendship.getTargetUserId(), user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих подтверждения запросов на дружбу");
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setCreatedAt(LocalDateTime.now());

        User subscriber = userRepository.findById(actionFriendship.getTargetUserId()).orElseThrow(UserNotFoundException::new);

        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setTargetUser(subscriber);
        subscription.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
        friendshipRepository.save(friendship);
    }

    @Override
    public void declineFriendshipRequest(ActionFriendship actionFriendship) {
        User user = userRepository.findById(actionFriendship.getUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(actionFriendship.getTargetUserId(), user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих непринятия запросов на дружбу");
        }
        friendshipRepository.delete(friendship);
    }

    @Override
    public void removeFriend(ActionFriendship actionFriendship) {
        User user = userRepository.findById(actionFriendship.getUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        Friendship friendship = friendshipRepository.findByUserIdAndFriendId(actionFriendship.getTargetUserId(), user.getId());
        if (friendship == null) {
            throw new NullPointerException("У вас нет ожидающих непринятия запросов на дружбу");
        } else if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalArgumentException("У вас нет такого друга");
        }
        subscriptionRepository.delete(subscriptionRepository.findBySubscriberIdAndTargetUserId(actionFriendship.getUserId(), actionFriendship.getTargetUserId()));
        friendshipRepository.delete(friendship);
    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {

        List<User> users = serviceUtilities.checkUsersForMessaging(messageDTO.getSenderId(), messageDTO.getReceiverId());

        Message message = new Message();
        message.setSender(users.get(0));
        message.setReceiver(users.get(1));
        message.setContent(messageDTO.getContent());
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

        modelMapper.map(message, MessageDTO.class);
    }

    @Override
    public List<MessageDTO> getChat(ChatDTO chatDTO) {

        List<User> users = serviceUtilities.checkUsersForMessaging(chatDTO.getUser1Id(), chatDTO.getUser2Id());

        List<Message> chatMessages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtDesc(users.get(0), users.get(1), users.get(0), users.get(1));

        return chatMessages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .toList();
    }

    @Override
    public Page<PostDTO> getUserActivityFeed(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberId(userId);

        List<Long> targetUserIds = subscriptions.stream()
                .map(Subscription::getTargetUser)
                .map(User::getId)
                .toList();

        List<Post> activityFeedPosts = postRepository.findByUserIdIn(targetUserIds);

        List<PostDTO> postDTOList = activityFeedPosts.stream()
                .map(mapEntityToDTO::mapToPostDTO)
                .toList();

        return new PageImpl<>(postDTOList, pageable, activityFeedPosts.size());
    }

}



