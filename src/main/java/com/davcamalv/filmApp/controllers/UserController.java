package com.davcamalv.filmApp.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.dtos.ProfileDTO;
import com.davcamalv.filmApp.services.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController{

	protected final Logger log = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/getProfile")
	public ProfileDTO sendMessage(){
		log.info("POST /api/user/getProfile");
		return userService.getProfile();
	}
}
