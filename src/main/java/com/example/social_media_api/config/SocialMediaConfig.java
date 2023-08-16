package com.example.social_media_api.config;

import com.example.social_media_api.utilities.ServiceUtilities;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialMediaConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ServiceUtilities serviceUtilities() {
        return new ServiceUtilities();
    }
}
