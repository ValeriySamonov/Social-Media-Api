package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.dto.user.CreateUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@AutoConfigureMockMvc
public class UserControllerTest {

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

    @DisplayName("Тест для метода создания пользователя")
    @Test
    @SneakyThrows
    void createUserTest() {

        CreateUserDTO createUserDTO = new CreateUserDTO()
                .setUsername("user1")
                .setEmail("email@mail.com")
                .setPasswordHash("password1");

        String jsonCreateUser = new ObjectMapper().writeValueAsString(createUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateUser))
                .andExpect(status().isOk());
    }

    @DisplayName("Тест для метода создания пользователя при неправильном адресе email")
    @Test
    @SneakyThrows
    void createUserWithWrongEmailTest() {

        JSONObject jsonCreateUser = new JSONObject();
        jsonCreateUser.put("username", "user1");
        jsonCreateUser.put("passwordHash", "password1");
        jsonCreateUser.put("email", "email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateUser.toString()))
                .andExpect(status().isBadRequest());
    }
}
