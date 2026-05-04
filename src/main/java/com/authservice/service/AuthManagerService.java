package com.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthManagerService {

    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
    }
}
