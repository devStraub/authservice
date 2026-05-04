package com.jbase.repositories.interfaces;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbase.model.entities.OtpCode;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {
	
	Optional<OtpCode> findTopByEmailOrderByIdDesc(String email);
	
}
