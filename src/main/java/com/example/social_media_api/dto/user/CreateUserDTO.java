package com.example.social_media_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateUserDTO {

    @NotEmpty(message = "Имя обязательно для заполнения")
    private String username;

    @Email(message = "Неправильный формат")
    private String email;

    @NotEmpty(message = "Имя обязательно для заполнения")
    private String passwordHash;
}
