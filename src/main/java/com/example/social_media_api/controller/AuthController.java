package com.example.social_media_api.controller;

import com.example.social_media_api.jwt.JwtRequest;
import com.example.social_media_api.jwt.JwtResponse;
import com.example.social_media_api.jwt.RefreshJwtRequest;
import com.example.social_media_api.service.auth.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Аутентификация", description = "Вход в систему и получение токенов")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @Operation(summary = "Аутентификация в системе", description = "Введите правильное имя пользователя (логин) и пароль. " +
            "При корректной аутентификации будет получен access token и refresh token.")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authServiceImpl.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Новый токен доступа", description = "Получение нового access токена")
    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authServiceImpl.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Новый токен обновления", description = "Получение нового refresh токена")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authServiceImpl.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

}
