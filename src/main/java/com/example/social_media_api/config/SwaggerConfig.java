package com.example.social_media_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Social Media API")
                        .version("1.0")
                        .description("API для социальной медиа платформы, " +
                                "позволяющей пользователям регистрироваться, " +
                                "входить в систему, создавать посты, переписываться, " +
                                "подписываться на других пользователей и получать свою ленту активности."));
    }
}
