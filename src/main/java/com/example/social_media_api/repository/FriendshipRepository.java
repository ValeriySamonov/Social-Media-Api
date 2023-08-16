package com.example.social_media_api.repository;

import com.example.social_media_api.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Friendship findByUserIdAndFriendId(Long userId, Long friendId);
}
