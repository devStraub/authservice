package com.authservice.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authservice.dto.AuthRequestDTO;
import com.authservice.dto.AuthResponseDTO;
import com.authservice.dto.OtpValidateDTO;
import com.authservice.dto.RegisterRequestDTO;
import com.authservice.model.entities.User;
import com.authservice.model.enums.Provider;
import com.authservice.model.enums.Role;
import com.authservice.repositories.interfaces.UserRepository;
import com.authservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtService jwtService;
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final OtpService otpService;

	public AuthResponseDTO authenticate(AuthRequestDTO request) {

		User user = repository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Credenciais inválidas");
		}
		
		return generateToken(user);
	}

	public AuthResponseDTO register(RegisterRequestDTO request) {

		validateRegister(request);

		User user = User.builder().email(request.getEmail().toLowerCase())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.USER).provider(Provider.LOCAL)
				.build();

		repository.save(user);

		return generateToken(user);
	}

	private void validateRegister(RegisterRequestDTO request) {

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new RuntimeException("Email obrigatório");
		}

		if (request.getPassword() == null || request.getPassword().length() < 6) {
			throw new RuntimeException("Senha deve ter pelo menos 6 caracteres");
		}

		repository.findByEmail(request.getEmail()).ifPresent(user -> {
			throw new RuntimeException("Usuário já existe");
		});
	}

	private AuthResponseDTO generateToken(User user) {

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword() != null ? user.getPassword() : "",
				List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

		String token = jwtService.generateToken(userDetails);

		return new AuthResponseDTO(token);
	}

	public AuthResponseDTO authenticateOAuth(String email, String providerId, Provider provider) {

		User user = repository.findByEmail(email).orElseGet(() -> {
			User newUser = User.builder().email(email).provider(provider).providerId(providerId).role(Role.USER)
					.build();

			return repository.save(newUser);
		});

		return generateToken(user);
	}

	public AuthResponseDTO authenticateByOtp(String email) {

		User user = repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		return generateToken(user);
	}
	
	public void requestOtp(String email) {
	    otpService.generateOtp(email);
	}
	
	public AuthResponseDTO validateOtp(OtpValidateDTO request) {

	    boolean valid = otpService.validateOtp(request.getEmail(), request.getCode());

	    if (!valid) {
	        throw new RuntimeException("Código inválido");
	    }

	    User user = repository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	    return generateToken(user);
	}

}
