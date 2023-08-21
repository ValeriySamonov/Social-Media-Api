package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.container.BaseIntegrationContainer;
import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.UserRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc

public class UserControllerTest extends BaseIntegrationContainer {

    @Autowired
    MockMvc  mockMvc;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Тест для метода создания пользователя")
    @Test
    @SneakyThrows

    void createUserTest() {

        CreateUserDTO createUserDTO = new CreateUserDTO()
                .setUsername("user1")
                .setEmail("user1@example.com")
                .setPassword("password1");

        String jsonCreateUser = new ObjectMapper().writeValueAsString(createUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreateUser))
                .andExpect(status().isOk());

        Optional<User> savedUser = userRepository.findByUsername("user1");
        assertTrue(savedUser.isPresent());
        assertEquals(createUserDTO.getEmail(), savedUser.get().getEmail());
        assertEquals(createUserDTO.getPassword(), savedUser.get().getPassword());

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
                .andExpect(status().isConflict());
    }
}
