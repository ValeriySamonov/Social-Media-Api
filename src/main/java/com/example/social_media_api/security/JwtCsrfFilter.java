package com.example.social_media_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtCsrfFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

            request.setAttribute(HttpServletResponse.class.getName(), response);

            CsrfToken csrfToken = tokenRepository.loadToken(request);
            if (csrfToken == null) {
                csrfToken = tokenRepository.generateToken(request);
                tokenRepository.saveToken(csrfToken, request, response);
            }

            request.setAttribute(CsrfToken.class.getName(), csrfToken);
            request.setAttribute(csrfToken.getParameterName(), csrfToken);

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

}
