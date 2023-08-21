package com.example.social_media_api.data;

import com.example.social_media_api.model.User;
import com.example.social_media_api.security.SocialMediaUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GetAuthentication {

    public void createAuthentication() {

        SocialMediaUserDetails userDetails;
            userDetails = new SocialMediaUserDetails(new User(1L,"user1", "user1@example.com", "password1"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
