package com.example.social_media_api.repository;

import com.example.social_media_api.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findBySubscriberIdAndTargetUserId(Long subscriberId, Long targetUserId);
    List<Subscription> findBySubscriberId(Long subscriberId);

}
