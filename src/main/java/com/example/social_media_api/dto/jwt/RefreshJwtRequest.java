package com.example.social_media_api.dto.jwt;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class RefreshJwtRequest {

    private String refreshToken;

}
