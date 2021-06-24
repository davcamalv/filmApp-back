package com.davcamalv.filmApp.controllers;

import javax.validation.Valid;

import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/new")
	public ResponseEntity<?> newUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
		ResponseEntity<?> responseEntity = null;
		if (bindingResult.hasErrors()) {
			responseEntity = new ResponseEntity<Object>(new ErrorMessage("Incorrect attributes"),
					HttpStatus.BAD_REQUEST);
			return responseEntity;
		} else if (userService.existsByUsername(newUserDTO.getUsername())) {
			responseEntity = new ResponseEntity<Object>(new ErrorMessage("Username alredy exists"),
					HttpStatus.BAD_REQUEST);
		} else {
			userService.createNewUser(newUserDTO);
			responseEntity = new ResponseEntity<Object>(new ErrorMessage("User created"), HttpStatus.CREATED);
		}
		return responseEntity;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("/login")
	public ResponseEntity<JwtDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult bindingResult) {
		ResponseEntity<JwtDTO> responseEntity = null;
		if (bindingResult.hasErrors()) {
			responseEntity = new ResponseEntity(new ErrorMessage("Incorrect attributes"), HttpStatus.BAD_REQUEST);
			return responseEntity;
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		JwtDTO jwtDTO = new JwtDTO(jwt, userDetails.getUsername(), userDetails.getAuthorities());
		return new ResponseEntity(jwtDTO, HttpStatus.OK);
	}
}
