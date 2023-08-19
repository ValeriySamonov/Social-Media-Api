package com.example.social_media_api.utilities;

import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CheckUp {

    private final UserRepository userRepository;

    public List<User> checkUsersForMessaging(Long userId1, Long userId2) {

        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Нельзя отправлять сообщение самому себе.");
        }

        List<User> users = new ArrayList<>();
        User sender = userRepository.findById(userId1).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(userId2).orElseThrow(UserNotFoundException::new);
        users.add(0, sender);
        users.add(1, receiver);

        return users;
    }
}
