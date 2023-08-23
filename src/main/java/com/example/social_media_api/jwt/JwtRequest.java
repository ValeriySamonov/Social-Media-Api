package com.example.social_media_api.jwt;

import lombok.*;


@Data
@AllArgsConstructor
public class JwtRequest {

    private String username;
    private String password;

}

