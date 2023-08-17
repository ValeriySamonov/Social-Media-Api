package com.example.social_media_api.controller;

import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.service.SocialMediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final SocialMediaService socialMediaService;

    public UserController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    // Создание пользователя
    @PostMapping()
    public ResponseEntity<String> createUser(@Validated @RequestBody CreateUserDTO createUserDTO) {
        socialMediaService.createUser(createUserDTO);
        return ResponseEntity.ok("Пользователь успешно создан");
    }
}
