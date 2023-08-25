package com.example.social_media_api.service.auth;

import com.example.social_media_api.jwt.JwtAuthentication;
import com.example.social_media_api.dto.jwt.JwtRequest;
import com.example.social_media_api.dto.jwt.JwtResponse;
import jakarta.security.auth.message.AuthException;

public interface AuthService {

    JwtResponse login(JwtRequest authRequest) throws AuthException;
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refresh(String refreshToken) throws AuthException;
    JwtAuthentication getAuthInfo();
}
