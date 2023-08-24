package com.example.social_media_api.controller;

import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Регистрация пользователя")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Создание пользователя
    @Operation(summary = "Создание нового пользователя", description = "Пользователи могут зарегистрироваться, указав имя пользователя, электронную почту и пароль.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Пользователь с таким именем уже существует"),
            @ApiResponse(responseCode = "409", description = "Данные вводятся в неверном формате")
    })
    @PostMapping("/users")
    public Long createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO);
    }
}
