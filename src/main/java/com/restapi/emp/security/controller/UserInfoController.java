package com.restapi.emp.security.controller;

import com.restapi.emp.security.controller.dto.AuthRequest;
import com.restapi.emp.security.controller.dto.AuthResponse;
import com.restapi.emp.security.jwt.JwtService;
import com.restapi.emp.security.model.UserInfo;
import com.restapi.emp.security.model.UserInfoRepository;
import com.restapi.emp.security.service.UserInfoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/userinfos")
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
			String token = jwtService.generateToken(authRequest.getEmail());
			AuthResponse authResponse = new AuthResponse(token);
			authResponse.setUser(authRequest.getEmail());

			Optional<UserInfo> optionalUser =
					repository.findByEmail(authRequest.getEmail());
			if(optionalUser.isPresent()){
				UserInfo userEntity = optionalUser.get();
				authResponse.setRoles(userEntity.getRoles());
			}
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}
	}

//	@PostMapping("/new")
//	public String addNewUser(@RequestBody UserInfo userInfo){
//		return service.addUser(userInfo);
//	}

	@PostMapping("/new")
	public String addNewUser(@RequestBody UserInfo userInfo){
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		UserInfo savedUserInfo = repository.save(userInfo);
		return savedUserInfo.getName() + " user added!!";
	}
}