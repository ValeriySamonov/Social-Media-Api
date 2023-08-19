package com.example.social_media_api.controller;

import com.example.social_media_api.SocialMediaApiApplication;
import com.example.social_media_api.container.BaseIntegrationContainer;
import com.example.social_media_api.dto.message.ChatDTO;
import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.model.Message;
import com.example.social_media_api.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SocialMediaApiApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-test.sql") // Путь к скрипту с тестовыми данными
public class MessageControllerTest extends BaseIntegrationContainer {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MessageRepository messageRepository;

    @DisplayName("Тест для метода отправки сообщения")
    @Test
    @SneakyThrows
    void sendMessageTest() {

        MessageDTO messageDTO = new MessageDTO()
                .setSenderId(1L)
                .setReceiverId(3L)
                .setContent("Text");

        String jsonMessage = new ObjectMapper().writeValueAsString(messageDTO);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isOk());

        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(1L, 3L);
        assertFalse(messages.isEmpty());
        assertEquals("Text", messages.get(0).getContent());

    }

    @DisplayName("Тест для чата")
    @Test
    @SneakyThrows
    public void testGetChat() {

        ChatDTO chatDTO = new ChatDTO()
                .setUser1Id(1L)
                .setUser2Id(2L);

        MessageDTO message1 = new MessageDTO()
                .setSenderId(chatDTO.getUser1Id())
                .setReceiverId(chatDTO.getUser2Id())
                .setContent("message1");

        MessageDTO message2 = new MessageDTO()
                .setSenderId(chatDTO.getUser2Id())
                .setReceiverId(chatDTO.getUser1Id())
                .setContent("message2");

        List<MessageDTO> chat = Arrays.asList(message1, message2);

        String jsonChat = new ObjectMapper().writeValueAsString(chatDTO);

        mockMvc.perform(get("/api/messages/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonChat))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(chat.size()));

    }
}
