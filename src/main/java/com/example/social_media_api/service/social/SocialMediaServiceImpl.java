package com.example.social_media_api.service.social;

import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.PostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.exception.SubscriptionDoesNotExistException;
import com.example.social_media_api.exception.UserCanNotSubscribeException;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.jwt.JwtAuthentication;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.PostImage;
import com.example.social_media_api.model.Subscription;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.PostImageRepository;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
import com.example.social_media_api.repository.UserRepository;
import com.example.social_media_api.service.auth.AuthServiceImpl;
import com.example.social_media_api.utilities.ImageFileAction;
import com.example.social_media_api.utilities.mapper.MapEntityToDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service()
@RequiredArgsConstructor
public class SocialMediaServiceImpl implements SocialMediaService {

    static final int PAGE_SIZE = 10;

    private final ImageFileAction imageFileAction;
    private final MapEntityToDTO mapEntityToDTO;

    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AuthServiceImpl authServiceImpl;

    @Override
    public Long createPost(CreatePostDTO createPostDTO, List<MultipartFile> files) {

        User user = userRepository.findById(getAuthenticatedUserId()).orElseThrow(UserNotFoundException::new);
        Post post = new Post()
                .setText(createPostDTO.getText())
                .setTitle(createPostDTO.getTitle())
                .setUser(user)
                .setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        if (!CollectionUtils.isEmpty(files)) {
            imageFileAction.saveFiles(files, post);
        }
        return post.getId();
    }

    @Override
    public Page<PostDTO> getPostByUserId(Long postOwnerId, int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Post> postsPage = postRepository.findByUserId(postOwnerId, pageable);

        return postsPage.map(mapEntityToDTO::mapToPostDTO);

    }

    @Override
    public void updatePost(Long postId, UpdatePostDTO updatePostDTO, List<MultipartFile> addedFiles) {

        User user = userRepository.findById(getAuthenticatedUserId()).orElseThrow(UserNotFoundException::new);
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
                imageFileAction.deleteFileFromDirectory(removedImage.getFileName());
            }
        }

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {

        User user = userRepository.findById(getAuthenticatedUserId()).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByUserIdAndId(user.getId(), postId);

        List<PostImage> removedImages = post.getImages();
        for (PostImage removedImage : removedImages) {
            imageFileAction.deleteFileFromDirectory(removedImage.getFileName());
        }
        postImageRepository.deleteAll(removedImages);
        postRepository.delete(post);


    }

    @Override
    public void sendFriendshipRequest(FriendshipDTO friendshipDTO) {

        User subscriber = userRepository.findById(getAuthenticatedUserId()).orElseThrow(UserNotFoundException::new); // Получите текущего пользователя (подписчика)
        User targetUser = userRepository.findById(friendshipDTO.getTargetUserId()).orElseThrow(UserNotFoundException::new);

        if (subscriber.getId().equals(targetUser.getId())) {
            throw new UserCanNotSubscribeException();
        }

        Subscription subscription = new Subscription()
                .setSubscriber(subscriber)
                .setTargetUser(targetUser)
                .setCreatedAt(LocalDateTime.now())
                .setFriendStatus(FriendStatus.UNACCEPTED)
                .setSubStatus(SubStatus.USER1);

        subscriptionRepository.save(subscription);

    }

    @Override
    public void acceptFriendshipRequest(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetUserIdAndFriendStatus(
                friendshipDTO.getTargetUserId(), getAuthenticatedUserId(), FriendStatus.UNACCEPTED);

        if (subscription == null) {
            throw new SubscriptionDoesNotExistException();
        }

        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFriendStatus(FriendStatus.ACCEPTED);
        subscription.setSubStatus(SubStatus.BOTH);

        subscriptionRepository.save(subscription);
    }


    @Override
    public void declineFriendshipRequest(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetUserIdAndFriendStatus(
                friendshipDTO.getTargetUserId(), getAuthenticatedUserId(), FriendStatus.UNACCEPTED);

        if (subscription == null) {
            throw new SubscriptionDoesNotExistException();
        }

        subscription.setFriendStatus(FriendStatus.DECLINE);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void removeFriend(FriendshipDTO friendshipDTO) {

        Subscription subscription = subscriptionRepository.findSubscriptionsWithFriendStatus(
                getAuthenticatedUserId(), friendshipDTO.getTargetUserId(), FriendStatus.ACCEPTED);

        if (subscription == null) {
            throw new SubscriptionDoesNotExistException();
        }

        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setFriendStatus(FriendStatus.UNACCEPTED);

        if (Objects.equals(subscription.getSubscriber().getId(), getAuthenticatedUserId())) {
            subscription.setSubStatus(SubStatus.USER2);
        } else {
            subscription.setSubStatus(SubStatus.USER1);
        }

        subscriptionRepository.save(subscription);

    }

    @Override
    public Page<PostDTO> getUserActivityFeed(int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());

        List<Subscription> subscriptions = subscriptionRepository.findBySubscriberIdAndSubscriptionStatusIn(getAuthenticatedUserId(), SubStatus.USER1, SubStatus.BOTH);

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

    private Long getAuthenticatedUserId() {
        JwtAuthentication authInfo = authServiceImpl.getAuthInfo();
        return userRepository.findByUsername(authInfo.getUsername()).orElseThrow(UserNotFoundException::new).getId();
    }

}



