package com.authservice.repositories.interfaces;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authservice.model.entities.OtpCode;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {
	
	Optional<OtpCode> findTopByEmailOrderByIdDesc(String email);
	
}
