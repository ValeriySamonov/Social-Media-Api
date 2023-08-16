package com.example.social_media_api.repository;

import com.example.social_media_api.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
}
