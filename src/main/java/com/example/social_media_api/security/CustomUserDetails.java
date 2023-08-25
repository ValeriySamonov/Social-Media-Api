package com.example.social_media_api.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {

    Long getUserId();
}
