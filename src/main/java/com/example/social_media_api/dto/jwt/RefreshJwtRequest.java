package com.example.social_media_api.dto.jwt;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshJwtRequest {

    public String refreshToken;

}
