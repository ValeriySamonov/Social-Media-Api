package com.example.social_media_api.data;

import com.example.social_media_api.jwt.JwtAuthentication;
import com.example.social_media_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GetAuthentication {

    @Autowired
    UserRepository userRepository;

    public void createAuthentication() {

        JwtAuthentication authentication = new JwtAuthentication(true, "1");

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
