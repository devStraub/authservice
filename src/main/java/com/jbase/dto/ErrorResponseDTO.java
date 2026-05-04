package com.jbase.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponseDTO {
	
	private int status;
	private String message;
	private LocalDateTime timestamp;
	private String path;
	
}
