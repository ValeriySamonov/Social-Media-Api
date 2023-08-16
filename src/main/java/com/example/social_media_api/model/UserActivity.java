package com.example.social_media_api.model;

import com.example.social_media_api.enums.ActivityType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activities")
@Data
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private ActivityType activityType;
    private Long activityId;
    private LocalDateTime createdAt;

}
