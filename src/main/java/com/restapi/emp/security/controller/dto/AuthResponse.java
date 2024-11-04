package com.restapi.emp.security.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String user;
    private String[] roles;
    private String refreshToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}