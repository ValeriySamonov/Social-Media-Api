package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.dto.friendship.FriendshipDTO;
import com.example.social_media_api.dto.post.CreatePostDTO;
import com.example.social_media_api.dto.post.DeletePostDTO;
import com.example.social_media_api.dto.post.UpdatePostDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test.sql") // Путь к скрипту с тестовыми данными
public class SocialMediaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("hyantiv4");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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

        MockMultipartFile imageFile1 = new MockMultipartFile("addedFiles", "test1.jpg", "image/jpeg", "<<jpg data>>".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("addedFiles", "test2.jpg", "image/jpeg", "<<jpg data>>".getBytes());

        mockMvc.perform(multipart("/api/posts/" + postId)
                        .file(imageFile1)
                        .file(imageFile2)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .flashAttr("updatePostDTO", updatePostDTO))
                .andExpect(status().isOk());
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
                .setTargetUserId(2L);

        mockMvc.perform(post("/api/friendship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(friendshipDTO)))
                .andExpect(status().isOk());
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
