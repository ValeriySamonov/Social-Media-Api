package com.example.social_media_api.model;

import com.example.social_media_api.enums.FriendStatus;
import com.example.social_media_api.enums.SubStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "subscriptions")
@Data
@Accessors(chain = true)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Column(name = "friend_status")
    private FriendStatus friendStatus;

    @Column(name = "subs_status")
    private SubStatus subStatus;

}

