package com.example.social_media_api.dto;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String username;
    private String email;
    private String passwordHash;
}
