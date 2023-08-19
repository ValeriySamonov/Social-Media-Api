package com.example.social_media_api.repository;

import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import com.example.social_media_api.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findBySubscriberIdAndTargetUserIdAndFriendStatus(Long subscriberId, Long targetUserId, FriendStatus friendStatus);

    @Query("SELECT s FROM Subscription s " +
            "WHERE (s.subscriber.id = :x AND s.targetUser.id = :y AND s.friendStatus = :status) " +
            "OR (s.subscriber.id = :y AND s.targetUser.id = :x AND s.friendStatus = :status)")
    Subscription findSubscriptionsWithFriendStatus(@Param("x") Long x, @Param("y") Long y, @Param("status") FriendStatus status);

    @Query("SELECT s FROM Subscription s " +
            "WHERE (s.subscriber.id = :subscriberId " +
            "AND (s.subStatus = :status1 OR s.subStatus = :status2)) " +
            "OR (s.targetUser.id = :subscriberId " +
            "AND (s.subStatus = :status1 OR s.subStatus = :status2))")
    List<Subscription> findSubscriptionsBySubscriberIdAndSubscriptionStatusIn(@Param("subscriberId") Long subscriberId,
                                                                              @Param("status1") SubStatus status1,
                                                                              @Param("status2") SubStatus status2);

    @Query("SELECT s FROM Subscription s " +
            "WHERE (s.subscriber.id = :subscriberId AND s.targetUser.id = :targetUserId AND s.friendStatus = :friendStatus AND s.subStatus = :subStatus)")
    Optional<Subscription> findByUserIdAndTargetUserIdAndFriendStatusAndSubsStatus(long subscriberId, long targetUserId, FriendStatus friendStatus, SubStatus subStatus);
}
