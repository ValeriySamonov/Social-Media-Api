package com.example.social_media_api.service.message;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.exception.UsersAreNotFriendsException;
import com.example.social_media_api.model.Message;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.MessageRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
import com.example.social_media_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service()
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    public void sendMessage(MessageDTO messageDTO) {

        List<User> users = messageAbility(messageDTO.getSenderId(), messageDTO.getReceiverId());

        Message message = new Message();
        message.setSender(users.get(0));
        message.setReceiver(users.get(1));
        message.setContent(messageDTO.getContent());
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

    }

    private List<User> messageAbility(Long userId1, Long userId2) {

        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException();
        }

        if (subscriptionRepository.findSubscriptionsWithSubStatus(
                userId1, userId2, SubStatus.BOTH) == null) {
            throw new UsersAreNotFriendsException();
        }

        List<User> users = new ArrayList<>();
        User sender = userRepository.findById(userId1).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(userId2).orElseThrow(UserNotFoundException::new);
        users.add(sender);
        users.add(receiver);

        return users;

    }

}
