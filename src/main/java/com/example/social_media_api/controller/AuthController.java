package com.example.social_media_api.controller;

import com.example.social_media_api.dto.jwt.JwtRequest;
import com.example.social_media_api.dto.jwt.JwtResponse;
import com.example.social_media_api.dto.jwt.RefreshJwtRequest;
import com.example.social_media_api.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Аутентификация", description = "Вход в систему и получение токенов")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Аутентификация в системе", description = "Введите правильное имя пользователя (логин) и пароль. " +
            "При корректной аутентификации будет получен access token и getNewRefreshToken token.")
    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest authRequest) throws AuthException {
        return authService.login(authRequest);
    }

    @Operation(summary = "Новый токен доступа", description = "Получение нового access токена")
    @PostMapping("/token")
    public JwtResponse getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        return authService.getAccessToken(request.getRefreshToken());
    }

    @Operation(summary = "Новый токен обновления", description = "Получение нового getNewRefreshToken токена")
    @PostMapping("/refresh")
    public JwtResponse getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        return authService.getNewRefreshToken(request.getRefreshToken());
    }

}
