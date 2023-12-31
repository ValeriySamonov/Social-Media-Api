package com.example.social_media_api.service.auth;

import com.example.social_media_api.dto.jwt.JwtRequest;
import com.example.social_media_api.dto.jwt.JwtResponse;
import com.example.social_media_api.security.jwt.JwtAuthentication;
import com.example.social_media_api.security.jwt.JwtProvider;
import com.example.social_media_api.security.CustomUserDetails;
import com.example.social_media_api.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> refreshTokenStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    public JwtResponse login(@NonNull JwtRequest authRequest) throws AuthException {

        final CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserByUsername(authRequest.getUsername());

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {

            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);

            refreshTokenStorage.put(String.valueOf(user.getUserId()), refreshToken);

            return new JwtResponse(accessToken, refreshToken);

        } else {
            throw new AuthException("Ошибка аутентификации");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {

        if (jwtProvider.validateRefreshToken(refreshToken)) {

            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String userId = claims.getSubject();
            final String saveRefreshToken = refreshTokenStorage.get(userId);

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {

                final CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserById(Long.valueOf(userId));
                final String accessToken = jwtProvider.generateAccessToken(user);

                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse getNewRefreshToken(@NonNull String refreshToken) throws AuthException {

        if (jwtProvider.validateRefreshToken(refreshToken)) {

            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String userId = claims.getSubject();
            final String saveRefreshToken = refreshTokenStorage.get(userId);

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {

                final CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserById(Long.valueOf(userId));
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);

                refreshTokenStorage.put(String.valueOf(user.getUserId()), newRefreshToken);

                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
