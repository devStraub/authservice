package com.jbase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jbase.dto.AuthRequestDTO;
import com.jbase.dto.AuthResponseDTO;
import com.jbase.dto.OtpRequestDTO;
import com.jbase.dto.OtpValidateDTO;
import com.jbase.dto.RegisterRequestDTO;
import com.jbase.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/otp/request")
    public ResponseEntity<Void> requestOtp(@RequestBody OtpRequestDTO request) {

        authService.requestOtp(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/otp/validate")
    public ResponseEntity<AuthResponseDTO> validateOtp(@RequestBody OtpValidateDTO request) {

        return ResponseEntity.ok(authService.validateOtp(request));
    }
    
}
