package com.davcamalv.filmApp.controllers;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.dtos.JwtDTO;
import com.davcamalv.filmApp.dtos.NewUserDTO;
import com.davcamalv.filmApp.dtos.UserLoginDTO;
import com.davcamalv.filmApp.security.JwtProvider;
import com.davcamalv.filmApp.services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtProvider jwtProvider;

	@PostMapping("/new")
	public JwtDTO newUser(@RequestBody NewUserDTO newUserDTO) {
		userService.createNewUser(newUserDTO);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(newUserDTO.getUsername(), newUserDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		return new JwtDTO(jwt);
	}

	@PostMapping("/login")
	public JwtDTO login(@RequestBody UserLoginDTO userLoginDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		return new JwtDTO(jwt);
	}
	
	@PostMapping("/refresh")
	public JwtDTO refresh(@RequestBody JwtDTO jwtDTO) throws ParseException {
		String token = jwtProvider.refreshToken(jwtDTO);
		jwtDTO = new JwtDTO(token);
		return new JwtDTO(token);
	}
}
