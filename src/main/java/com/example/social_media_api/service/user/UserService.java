package com.example.social_media_api.service.user;

import com.example.social_media_api.dto.user.CreateUserDTO;

public interface UserService {
    Long createUser(CreateUserDTO createUserDTO);
}
