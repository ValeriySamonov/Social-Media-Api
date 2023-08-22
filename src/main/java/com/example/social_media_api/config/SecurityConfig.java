package com.example.social_media_api.config;

import com.example.social_media_api.security.JwtCsrfFilter;
import com.example.social_media_api.security.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtTokenRepository jwtTokenRepository;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterAt(new JwtCsrfFilter(jwtTokenRepository), CsrfFilter.class)
                .authorizeHttpRequests(this::customizeRequest)
                .csrf()
                .ignoringRequestMatchers("/api/users")
                .ignoringRequestMatchers("/login");

        return http.build();
    }

    protected void customizeRequest(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        try {
            registry.requestMatchers("/api/users")
                    .permitAll()
                    .requestMatchers("/api/posts/**")
                    .authenticated()
                    .requestMatchers("/api/friendship/**")
                    .authenticated()
                    .requestMatchers("/api/activity-feed")
                    .authenticated()
                    .requestMatchers("/api/messages")
                    .authenticated()
                    .and()
                    .formLogin().permitAll()
                    .and()
                    .logout().logoutUrl("/logout")
                    .and()
                    .httpBasic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

