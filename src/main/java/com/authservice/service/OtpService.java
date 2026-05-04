package com.authservice.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.authservice.model.entities.OtpCode;
import com.authservice.repositories.interfaces.OtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	private final OtpRepository otpRepository;
	private final EmailService emailService;

	public void generateOtp(String email) {

		String code = String.valueOf((int) (Math.random() * 900000) + 100000);

		OtpCode otp = new OtpCode();
		otp.setEmail(email);
		otp.setCode(code);
		otp.setExpiration(LocalDateTime.now().plusMinutes(5));
		otp.setUsed(false);

		otpRepository.save(otp);

		emailService.sendOtp(email, code);
	}

	public boolean validateOtp(String email, String code) {

		OtpCode otp = otpRepository.findTopByEmailOrderByIdDesc(email)
				.orElseThrow(() -> new RuntimeException("Código não encontrado"));

		if (otp.isUsed())
			return false;

		if (otp.getExpiration().isBefore(LocalDateTime.now()))
			return false;

		if (!otp.getCode().equals(code))
			return false;

		otp.setUsed(true);
		otpRepository.save(otp);

		return true;
	}
}
