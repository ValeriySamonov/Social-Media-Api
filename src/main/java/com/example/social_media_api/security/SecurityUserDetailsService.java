package com.example.social_media_api.security;

import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return new SocialMediaUserDetails(user);
    }
}
