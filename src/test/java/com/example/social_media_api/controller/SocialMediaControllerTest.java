package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.container.BaseIntegrationContainer;
import com.example.social_media_api.data.GetAuthentication;
import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.model.Post;
import com.example.social_media_api.model.Subscription;
import com.example.social_media_api.repository.PostRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
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

@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test-social.sql") // Путь к скрипту с тестовыми данными
public class SocialMediaControllerTest extends BaseIntegrationContainer {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    private GetAuthentication getAuthentication;

    @BeforeEach
    void prepareForTest() {
        getAuthentication.createAuthentication();
    }

    @AfterAll
    static void deleteFilesAndDirectory() throws IOException {
        deleteDirectory(new File("uploads/pictures_test"));
    }

    @DisplayName("Тест для метода создания поста")
    @Test
    @SneakyThrows
    public void createPostTest() {

        CreatePostDTO createPostDTO = new CreatePostDTO()
                .setUserId(1L)
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

        Optional<Post> savedPost = postRepository.findById(updatePostDTO.getUserId());
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

        Long postId = 1L;

        mockMvc.perform(delete("/api/posts")
                        .param("postId", String.valueOf(postId))
                        .contentType(MediaType.APPLICATION_JSON))
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

        Optional<Subscription> savedRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getUserId(), friendshipDTO.getTargetUserId(), FriendStatus.UNACCEPTED, SubStatus.USER1);

        assertTrue(savedRequest.isPresent());
        assertEquals(friendshipDTO.getUserId(), savedRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getTargetUserId(), savedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода принятия запроса на дружбу")
    @Test
    @SneakyThrows
    public void acceptFriendshipRequestTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(2L);

        mockMvc.perform(patch("/api/friendship/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> acceptedRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getTargetUserId(), friendshipDTO.getUserId(), FriendStatus.ACCEPTED, SubStatus.BOTH);

        assertTrue(acceptedRequest.isPresent());
        assertEquals(friendshipDTO.getTargetUserId(), acceptedRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getUserId(), acceptedRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода отклонения запроса на дружбу")
    @Test
    @SneakyThrows
    public void declineFriendshipRequestTest() {
        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(2L);

        mockMvc.perform(patch("/api/friendship/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getTargetUserId(), friendshipDTO.getUserId(), FriendStatus.DECLINE, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(friendshipDTO.getTargetUserId(), declineRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getUserId(), declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода удаления друга (отписка)")
    @Test
    @SneakyThrows
    public void removeFriendTest() {

        FriendshipDTO friendshipDTO = new FriendshipDTO()
                .setUserId(1L)
                .setTargetUserId(3L);

        mockMvc.perform(patch("/api/friendship/unfriend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());

        Optional<Subscription> declineRequest = subscriptionRepository.findByParamWithAnd(
                friendshipDTO.getUserId(), friendshipDTO.getTargetUserId(), FriendStatus.UNACCEPTED, SubStatus.USER2);

        assertTrue(declineRequest.isPresent());
        assertEquals(friendshipDTO.getUserId(), declineRequest.get().getSubscriber().getId());
        assertEquals(friendshipDTO.getTargetUserId(), declineRequest.get().getTargetUser().getId());
    }

    @DisplayName("Тест для метода просмотра ленты активности")
    @Test
    @SneakyThrows
    public void getUserActivityFeedTest() {

        int page = 0;

        mockMvc.perform(get("/api/activity-feed" + "?page=" + page))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    public static void deleteDirectory(File directory) throws IOException {
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


}
