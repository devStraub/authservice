package com.jbase.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.jbase.model.enums.Provider;
import com.jbase.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    public OAuthSuccessHandler(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        if (email == null) {
            email = oauthUser.getAttribute("preferred_username");
        }

        String providerId = oauthUser.getAttribute("sub");

        if (providerId == null) {
            providerId = oauthUser.getAttribute("oid");
        }

        Provider provider = resolveProvider(authentication);

        var authResponse = authService.authenticateOAuth(email, providerId, provider);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + authResponse.getToken() + "\"}");
    }

    private Provider resolveProvider(Authentication authentication) {

        String auth = authentication.getAuthorities().toString().toLowerCase();

        if (auth.contains("google")) return Provider.GOOGLE;
        if (auth.contains("microsoft")) return Provider.MICROSOFT;

        return Provider.LOCAL;
    }
}