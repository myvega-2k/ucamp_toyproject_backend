package com.restapi.emp.security.controller;

import com.restapi.emp.security.controller.dto.AuthRequest;
import com.restapi.emp.security.controller.dto.AuthResponse;
import com.restapi.emp.security.controller.dto.RefreshTokenRequest;
import com.restapi.emp.security.jwt.JwtRefreshTokenService;
import com.restapi.emp.security.jwt.JwtService;
import com.restapi.emp.security.model.UserInfo;
import com.restapi.emp.security.model.RefreshToken;
import com.restapi.emp.security.model.UserInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {
//	@Autowired
//	private UserInfoUserDetailsService service;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserInfoRepository repository;

	@Autowired
	private JwtRefreshTokenService jwtRefreshTokenService;


	@PostMapping("/login")
	public ResponseEntity<AuthResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		//AuthenticationManager 의 인증처리(authenticate) 요청
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authRequest.getEmail(),
						authRequest.getPassword()
				));
		//인증 성공 했어?
		if (authentication.isAuthenticated()) {
			String email = authRequest.getEmail();
			//AccessToken 생성
			String token = jwtService.generateToken(email);
			//AccessToken을 AuthResponse 객체에 저장
			AuthResponse authResponse = new AuthResponse(token);
			authResponse.setUser(email);

			Optional<UserInfo> optionalUser =
					repository.findByEmail(email);
			if(optionalUser.isPresent()){
				UserInfo userEntity = optionalUser.get();
				authResponse.setRoles(userEntity.getRoles().split(","));
			}
			//RefreshToken 생성
			RefreshToken refreshToken = jwtRefreshTokenService.createRefreshToken(email);
			authResponse.setRefreshToken(refreshToken.getToken());

			return new ResponseEntity<>(authResponse, HttpStatus.OK);
		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}
	}

	@PostMapping("/new")
	public String addNewUser(@RequestBody UserInfo userInfo){
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		UserInfo savedUserInfo = repository.save(userInfo);
		return savedUserInfo.getName() + " user added!!";
	}

	@PostMapping("/refreshToken")
	public AuthResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return jwtRefreshTokenService.findByToken(refreshTokenRequest.getRefreshToken())
				.map(jwtRefreshTokenService::verifyExpiration)
				.map(RefreshToken::getUserInfo)
				.map(userInfo -> {
					String accessToken = jwtService.generateToken(userInfo.getEmail());
					return AuthResponse.builder()
							.accessToken(accessToken)
							.refreshToken(refreshTokenRequest.getRefreshToken())
							.build();
				}).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
	}

	@GetMapping
	public ResponseEntity<List<UserInfo>> getUser() {
		return ResponseEntity.ok(repository.findAll());
	}
}