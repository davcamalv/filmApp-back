package com.davcamalv.filmApp.controllers;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.dtos.ProfileDTO;
import com.davcamalv.filmApp.dtos.ProfileDetailsDTO;
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
		log.info("GET /api/user/getProfile");
		return userService.getProfile();
	}
	
	@PostMapping("/addGenresToPrincipal")
	public ProfileDTO addGenresToPrincipal(@RequestBody List<Long> ids){
		log.info("POST /api/user/addGenresToPrincipal");
		return userService.addGenresToPrincipal(ids);
	}
	
	@PostMapping("/changeAvatar")
	public void changeAvatar(@RequestBody String avatar){
		log.info("POST /api/user/changeAvatar");
		userService.changeAvatar(avatar);
	}
	
	@PostMapping("/saveDetails")
	public ProfileDTO saveDetails(@RequestBody ProfileDetailsDTO profileDetailsDTO){
		log.info("POST /api/user/saveDetails");
		return userService.saveDetails(profileDetailsDTO);
	}
}
