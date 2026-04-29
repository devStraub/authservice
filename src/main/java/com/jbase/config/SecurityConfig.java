package com.jbase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.jbase.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	// =========================
	// TEST PROFILE (SEM OAUTH2)
	// =========================
	@Bean
	@Profile("test")
	public SecurityFilterChain securityFilterChainTest(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
				.userDetailsService(userDetailsService);

		return http.build();
	}

	// =========================
	// DEV / PROD (COM OAUTH2)
	// =========================
	@Bean
	@Profile({ "dev", "prod" })
	public SecurityFilterChain securityFilterChainOAuth(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
						.anyRequest().authenticated())
				.oauth2Login(oauth -> oauth.successHandler((request, response, authentication) -> {

					response.setContentType("application/json");
					response.getWriter().write("{\"message\": \"Login com Google OK\"}");
				})).userDetailsService(userDetailsService);

		return http.build();
	}

	// =========================
	// BEANS GLOBAIS
	// =========================
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}