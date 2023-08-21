package com.example.social_media_api.utilities;

import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.exception.UsersAreNotFriendsException;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.SubscriptionRepository;
import com.example.social_media_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class checkMessageAbility {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<User> messageAbility(Long userId1, Long userId2) {

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
