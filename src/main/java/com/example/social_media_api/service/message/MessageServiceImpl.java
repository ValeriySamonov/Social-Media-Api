package com.example.social_media_api.service.message;

import com.example.social_media_api.dto.message.MessageDTO;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.exception.UserCanNotWriteException;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.exception.UsersAreNotFriendsException;
import com.example.social_media_api.security.jwt.JwtAuthentication;
import com.example.social_media_api.model.Message;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.MessageRepository;
import com.example.social_media_api.repository.SubscriptionRepository;
import com.example.social_media_api.repository.UserRepository;
import com.example.social_media_api.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service()
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public void sendMessage(MessageDTO messageDTO) {

        List<User> users = messageAbility(getAuthenticatedUserId(), messageDTO.getReceiverId());

        Message message = new Message()
                .setSender(users.get(0))
                .setReceiver(users.get(1))
                .setContent(messageDTO.getContent())
                .setSentAt(LocalDateTime.now());

        messageRepository.save(message);

    }

    private List<User> messageAbility(Long userId1, Long userId2) {

        if (userId1.equals(userId2)) {
            throw new UserCanNotWriteException();
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

    private Long getAuthenticatedUserId() {
        JwtAuthentication authInfo = authService.getAuthInfo();
        return Long.valueOf(authInfo.getName());
    }

}
