package com.example.social_media_api.service.message;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.model.Message;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.MessageRepository;
import com.example.social_media_api.utilities.checkMessageAbility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("userDetailsService2")
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final checkMessageAbility checkMessageAbility;
    @Override
    public void sendMessage(MessageDTO messageDTO) {

        List<User> users = checkMessageAbility.messageAbility(messageDTO.getSenderId(), messageDTO.getReceiverId());

        Message message = new Message();
        message.setSender(users.get(0));
        message.setReceiver(users.get(1));
        message.setContent(messageDTO.getContent());
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

    }

}
