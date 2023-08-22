package com.example.social_media_api.data;

import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.repository.UserRepository;
import com.example.social_media_api.security.SocialMediaUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GetAuthentication {

    @Autowired
    UserRepository userRepository;

    public void createAuthentication() {

        SocialMediaUserDetails userDetails;
            userDetails = new SocialMediaUserDetails(userRepository.findById(1L).orElseThrow(UserNotFoundException::new));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
