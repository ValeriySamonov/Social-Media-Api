package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.container.BaseIntegrationContainer;
import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.DeletePostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.Subscription;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test.sql") // Путь к скрипту с тестовыми данными
public class SocialMediaControllerTest extends BaseIntegrationContainer {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @DisplayName("Тест для метода создания поста")
    @Test
    @SneakyThrows
    public void createPostTest() {

        CreatePostDTO createPostDTO = new CreatePostDTO()
                .setCreatorId(1L)
                .setTitle("Title")
                .setText("Text");

        MockMultipartFile imageFile1 = new MockMultipartFile("file1", "image1.png", "image/png", "image content 1".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("file2", "image2.png", "image/png", "image content 2".getBytes());

        mockMvc.perform(multipart("/api/posts")
                        .file(imageFile1)
                        .file(imageFile2)
                        .flashAttr("createPostDTO", createPostDTO))
                .andExpect(status().isOk());

        Optional<Post> savedPost = postRepository.findById(2L);
        assertTrue(savedPost.isPresent());
        assertEquals("Title", savedPost.get().getTitle());
        assertEquals("Text", savedPost.get().getText());

    }

    @DisplayName("Тест для метода редактирования поста")
    @Test
    @SneakyThrows
    public void updatePostTest() {

        long postId = 1L;

        UpdatePostDTO updatePostDTO = new UpdatePostDTO()
                .setUserId(1L)
                .setTitle("Updated Title")
                .setText("Updated Text")
                .setRemovedFileIds(Arrays.asList(1L, 2L));

        MockMultipartFile imageFile1 = new MockMultipartFile(
                "addedFiles",
                "test1.jpg",
                "image/jpeg",
                "<<jpg data>>".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile(
                "addedFiles",
                "test2.jpg",
                "image/jpeg",
                "<<jpg data>>".getBytes());

        mockMvc.perform(multipart("/api/posts/" + postId)
                        .file(imageFile1)
                        .file(imageFile2)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .flashAttr("updatePostDTO", updatePostDTO))
                .andExpect(status().isOk());

        Optional<Post> savedPost = postRepository.findById(1L);
        assertTrue(savedPost.isPresent());
        assertEquals("Updated Title", savedPost.get().getTitle());
        assertEquals("Updated Text", savedPost.get().getText());
    }

    @DisplayName("Тест для метода постраничного вывода постов пользователя")
    @Test
    @SneakyThrows
    public void getPostByUserIdTest() {

        long userId = 1L;
        int page = 0;

        mockMvc.perform(get("/api/posts/" + userId + "?page=" + page))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @DisplayName("Тест для метода удаления поста пользователя")
    @Test
    @SneakyThrows
    public void deletePostTest() {

        DeletePostDTO deletePostDTO = new DeletePostDTO()
                .setUserId(1L)
                .setPostId(1L);

        mockMvc.perform(delete("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(deletePostDTO)))
                .andExpect(status().isOk());
    }

    @DisplayName("Тест для метода отправки запроса на дружбу")
    @Test
    @SneakyThrows
    public void sendFriendshipRequestTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(3L);

        mockMvc.perform(post("/api/friendship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> savedRequest = subscriptionRepository.findByUserIdAndTargetUserIdAndFriendStatusAndSubsStatus(
                1L, 3L, FriendStatus.UNACCEPTED, SubStatus.USER1);

        assertTrue(savedRequest.isPresent());
        assertEquals(1L, savedRequest.get().getSubscriber().getId());
        assertEquals(3L, savedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода принятия запроса на дружбу")
    @Test
    @SneakyThrows
    public void acceptFriendshipRequestTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(2L);

        mockMvc.perform(post("/api/friendship/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> acceptedRequest = subscriptionRepository.findByUserIdAndTargetUserIdAndFriendStatusAndSubsStatus(
                2L, 1L, FriendStatus.ACCEPTED, SubStatus.BOTH);

        assertTrue(acceptedRequest.isPresent());
        assertEquals(2L, acceptedRequest.get().getSubscriber().getId());
        assertEquals(1L, acceptedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода отклонения запроса на дружбу")
    @Test
    @SneakyThrows
    public void declineFriendshipRequestTest() {
        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(2L);

        mockMvc.perform(post("/api/friendship/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByUserIdAndTargetUserIdAndFriendStatusAndSubsStatus(
                2L, 1L, FriendStatus.UNACCEPTED, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(2L, declineRequest.get().getSubscriber().getId());
        assertEquals(1L, declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода удаления друга (отписка)")
    @Test
    @SneakyThrows
    public void removeFriendTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(3L);

        mockMvc.perform(post("/api/friendship/unfriend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByUserIdAndTargetUserIdAndFriendStatusAndSubsStatus(
                1L, 3L, FriendStatus.UNACCEPTED, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(1L, declineRequest.get().getSubscriber().getId());
        assertEquals(3L, declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода просмотра ленты активности")
    @Test
    @SneakyThrows
    public void getUserActivityFeedTest() {

        long userId = 1L;
        int page = 0;

        mockMvc.perform(get("/api/activity-feed/" + userId + "?page=" + page))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }
}
