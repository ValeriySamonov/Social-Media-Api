package com.example.social_media_api.jwt;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class RefreshJwtRequest {

    public String refreshToken;

}
