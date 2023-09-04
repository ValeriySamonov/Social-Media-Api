package com.example.social_media_api.controller;

import com.example.social_media_api.TestSocialMediaApiApplication;
import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.jwt.JwtRequest;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.Subscription;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
import com.example.social_media_api.service.auth.AuthService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestSocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test-social.sql") // Путь к скрипту с тестовыми данными
public class SocialMediaControllerTest {
    private static String accessToken;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    AuthService authService;

    @BeforeEach
    void prepareForTest() {
        getAccessToken();
    }

    @AfterAll
    static void deleteFilesAndDirectory() {
        deleteDirectory(new File("uploads/pictures_test"));
    }

    @DisplayName("Тест для метода создания поста")
    @Test
    @SneakyThrows
    public void createPostTest() {

        CreatePostDTO createPostDTO = new CreatePostDTO()
                .setTitle("Title")
                .setText("Text");

        MockMultipartFile imageFile1 = new MockMultipartFile("file1", "image1.png", "image/png", "image content 1".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("file2", "image2.png", "image/png", "image content 2".getBytes());

        mockMvc.perform(multipart("/api/posts")
                        .file(imageFile1)
                        .file(imageFile2)
                        .header("Authorization", "Bearer " + accessToken)
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

        long userId = 1L;
        long postId = 1L;

        UpdatePostDTO updatePostDTO = new UpdatePostDTO()
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
                        .header("Authorization", "Bearer " + accessToken)
                        .flashAttr("updatePostDTO", updatePostDTO))
                .andExpect(status().isOk());

        Optional<Post> savedPost = postRepository.findById(userId);
        assertTrue(savedPost.isPresent());
        assertEquals("Updated Title", savedPost.get().getTitle());
        assertEquals("Updated Text", savedPost.get().getText());

    }

    @DisplayName("Тест для метода постраничного вывода постов пользователя")
    @Test
    @SneakyThrows
    public void getPostByUserIdTest() {

        long postOwnerId = 1L;
        int page = 0;
        int pageSize = 10;

        mockMvc.perform(get("/api/posts/" + postOwnerId + "?page=" + page + "&pageSize=" + pageSize)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @DisplayName("Тест для метода удаления поста пользователя")
    @Test
    @SneakyThrows
    public void deletePostTest() {

        long postId = 1L;

        mockMvc.perform(delete("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Тест для метода отправки запроса на дружбу")
    @Test
    @SneakyThrows
    public void sendFriendshipRequestTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setTargetUserId(3L);

        long userId = 1L;

        mockMvc.perform(patch("/api/friendship/request")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> savedRequest = subscriptionRepository.findByParamWithAnd(
                userId, friendshipDTO.getTargetUserId(), FriendStatus.UNACCEPTED, SubStatus.USER1);

        assertTrue(savedRequest.isPresent());
        assertEquals(userId, savedRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getTargetUserId(), savedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода принятия запроса на дружбу")
    @Test
    @SneakyThrows
    public void acceptFriendshipRequestTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setTargetUserId(2L);

        long userId = 1L;

        mockMvc.perform(patch("/api/friendship/accept")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> acceptedRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getTargetUserId(), userId, FriendStatus.ACCEPTED, SubStatus.BOTH);

        assertTrue(acceptedRequest.isPresent());
        assertEquals(friendshipDTO.getTargetUserId(), acceptedRequest.get().getSubscriber().getId());
        assertEquals(userId, acceptedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода отклонения запроса на дружбу")
    @Test
    @SneakyThrows
    public void declineFriendshipRequestTest() {
        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setTargetUserId(2L);

        long userId = 1L;

        mockMvc.perform(patch("/api/friendship/reject")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getTargetUserId(), userId, FriendStatus.DECLINE, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(friendshipDTO.getTargetUserId(), declineRequest.get().getSubscriber().getId());
        assertEquals(userId, declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода удаления друга (отписка)")
    @Test
    @SneakyThrows
    public void removeFriendTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setTargetUserId(3L);

        long userId = 1L;

        mockMvc.perform(patch("/api/friendship/unfriend")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByParamWithAnd(
                userId, friendshipDTO.getTargetUserId(), FriendStatus.UNACCEPTED, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(userId, declineRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getTargetUserId(), declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода просмотра ленты активности")
    @Test
    @SneakyThrows
    public void getUserActivityFeedTest() {

        int page = 0;
        int pageSize = 10;

        mockMvc.perform(get("/api/activity-feed" + "?page=" + page + "&pageSize=" + pageSize)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }
@SneakyThrows
    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            Path path = directory.toPath();
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    @SneakyThrows
    private void getAccessToken() {
        JwtRequest authRequest = new JwtRequest("user1", "password1");
        accessToken = authService.login(authRequest).getAccessToken();
    }

}
