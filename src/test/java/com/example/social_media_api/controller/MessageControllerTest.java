package com.example.social_media_api.controller;

import com.example.social_media_api.TestSocialMediaApiApplication;
import com.example.social_media_api.dto.jwt.JwtRequest;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.model.Message;
import com.example.social_media_api.repository.MessageRepository;
import com.example.social_media_api.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test-message.sql") // Путь к скрипту с тестовыми данными
public class MessageControllerTest {

    private static String accessToken;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    AuthService authService;

    @BeforeEach
    void prepareForTest() {
        getAccessToken();
    }

    @DisplayName("Тест для метода отправки сообщения")
    @Test
    @SneakyThrows
    void sendMessageTest() {

        MessageDTO messageDTO = new MessageDTO()
                .setReceiverId(3L)
                .setContent("Text");

        long userId = 1L;
        String jsonMessage = new ObjectMapper().writeValueAsString(messageDTO);

        mockMvc.perform(post("/api/messages")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isOk());

        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(userId, messageDTO.getReceiverId());
        assertFalse(messages.isEmpty());
        assertEquals(messageDTO.getContent(), messages.get(0).getContent());

    }

    @SneakyThrows
    private void getAccessToken() {
        JwtRequest authRequest = new JwtRequest("user1", "password1");
        accessToken = authService.login(authRequest).getAccessToken();
    }

}
