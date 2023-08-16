package com.example.social_media_api.repository;

import com.example.social_media_api.model.Subscription;
import com.example.social_media_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findBySubscriberIdAndTargetUserId(Long subscriberId, Long targetUserId);
}
