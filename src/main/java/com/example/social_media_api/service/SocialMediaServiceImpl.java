package com.example.social_media_api.service;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.message.ChatDTO;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.DeletePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.exception.UserAlreadyExistsException;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.model.*;
import com.example.social_media_api.repository.*;
import com.example.social_media_api.utilities.CheckUp;
import com.example.social_media_api.utilities.mapper.MapEntityToDTO;
import com.example.social_media_api.utilities.ImageFileAction;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialMediaServiceImpl implements SocialMediaService {

    static final int PAGE_SIZE = 10;

    private final ImageFileAction imageFileAction;
    private final MapEntityToDTO mapEntityToDTO;
    private final CheckUp checkUp;

    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        String username = createUserDTO.getUsername();

        // Проверка наличия пользователя с таким именем
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = modelMapper.map(createUserDTO, User.class);
        userRepository.save(user);
    }


    @Override
    public void createPost(CreatePostDTO createPostDTO, List<MultipartFile> files) {
        User user = userRepository.findById(createPostDTO.getCreatorId()).orElseThrow(UserNotFoundException::new);
        Post post = new Post();
        post.setText(createPostDTO.getText());
        post.setTitle(createPostDTO.getTitle());
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        if (!CollectionUtils.isEmpty(files)) {
            imageFileAction.saveFiles(files, post);
        }

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
        Post post = postRepository.findByUserIdAndId(user.getId(), postId);

        post.setTitle(updatePostDTO.getTitle());
        post.setText(updatePostDTO.getText());

        if (!CollectionUtils.isEmpty(addedFiles)) {

            imageFileAction.saveFiles(addedFiles, post);
        }

        if (updatePostDTO.getRemovedFileIds() != null && !updatePostDTO.getRemovedFileIds().isEmpty()) {
            List<PostImage> removedImages = post.getImages().stream()
                    .filter(image -> updatePostDTO.getRemovedFileIds().contains(image.getId()))
                    .toList();

            post.getImages().removeAll(removedImages);
            postImageRepository.deleteAll(removedImages);

            for (PostImage removedImage : removedImages) {
                imageFileAction.deleteImageFromDirectory(removedImage.getFileName());
            }
        }

        postRepository.save(post);
    }

    @Override
    public void deletePost(DeletePostDTO deletePostDTO) {
        User user = userRepository.findById(deletePostDTO.getUserId()).orElseThrow(UserNotFoundException::new);
        try {
            Post post = postRepository.findByUserIdAndId(user.getId(), deletePostDTO.getPostId());

            List<PostImage> removedImages = post.getImages();
            for (PostImage removedImage : removedImages) {
                imageFileAction.deleteImageFromDirectory(removedImage.getFileName());
            }

            postRepository.delete(post);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendFriendshipRequest(FriendshipDTO friendshipDTO) {
        User subscriber = userRepository.findById(friendshipDTO.getUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        User targetUser = userRepository.findById(friendshipDTO.getTargetUserId()).orElseThrow(UserNotFoundException::new);

        if (subscriber.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException();
        }

        Subscription subscription = new Subscription();

        subscription.setSubscriber(subscriber);
        subscription.setTargetUser(targetUser);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFriendStatus(FriendStatus.UNACCEPTED);
        subscription.setSubStatus(SubStatus.USER1);

        subscriptionRepository.save(subscription);

    }

    @Override
    public void acceptFriendshipRequest(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetUserIdAndFriendStatus(
                friendshipDTO.getTargetUserId(), friendshipDTO.getUserId(), FriendStatus.UNACCEPTED);

        if (subscription == null) {
            throw new NullPointerException();
        }

        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFriendStatus(FriendStatus.ACCEPTED);
        subscription.setSubStatus(SubStatus.BOTH);

        subscriptionRepository.save(subscription);
    }


    @Override
    public void declineFriendshipRequest(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetUserIdAndFriendStatus(
                friendshipDTO.getTargetUserId(), friendshipDTO.getUserId(), FriendStatus.UNACCEPTED);

        if (subscription == null) {
            throw new NullPointerException();
        }

    }

    @Override
    public void removeFriend(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findSubscriptionsWithFriendStatus(
                friendshipDTO.getUserId(), friendshipDTO.getTargetUserId(), FriendStatus.ACCEPTED);

        if (subscription == null) {
            throw new NullPointerException();
        }

        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFriendStatus(FriendStatus.UNACCEPTED);

        if (Objects.equals(subscription.getSubscriber().getId(), friendshipDTO.getUserId())) {
            subscription.setSubStatus(SubStatus.USER2);
        } else {
            subscription.setSubStatus(SubStatus.USER1);
        }

        subscriptionRepository.save(subscription);

    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {

        List<User> users = checkUp.checkUsersForMessaging(messageDTO.getSenderId(), messageDTO.getReceiverId());

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

        List<User> users = checkUp.checkUsersForMessaging(chatDTO.getUser1Id(), chatDTO.getUser2Id());

        List<Message> chatMessages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtDesc(users.get(0), users.get(1), users.get(0), users.get(1));

        return chatMessages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .toList();
    }

    @Override
    public Page<PostDTO> getUserActivityFeed(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        List<Subscription> subscriptions = subscriptionRepository.findSubscriptionsBySubscriberIdAndSubscriptionStatusIn(userId, SubStatus.USER1, SubStatus.BOTH);

        List<Long> targetUserIds = subscriptions.stream()
                .map(Subscription::getTargetUser)
                .map(User::getId)
                .collect(Collectors.toList());

        List<Post> activityFeedPosts = postRepository.findByUserIdIn(targetUserIds);


        List<PostDTO> postDTOList = activityFeedPosts.stream()
                .map(mapEntityToDTO::mapToPostDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(postDTOList, pageable, activityFeedPosts.size());
    }

}



