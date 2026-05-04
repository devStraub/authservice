package com.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpValidateDTO {
	
	private String email;
	private String code;
	
}
