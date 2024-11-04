package com.restapi.emp.security.controller.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String user;
    private String[] roles;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}