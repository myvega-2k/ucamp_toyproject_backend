package com.restapi.emp.security.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
	@NotNull(message = "Email cannot be null")
	@Size(min = 2, message = "Email not be less than two characters")
	@Email
	private String email;
	//private String user;
	
	@NotNull(message = "Password cannot be null")
	@Size(min = 4, message = "Password must be equals or grater than 4 characters")
	private String password;
	//private String pwd;
}