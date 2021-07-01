package com.davcamalv.filmApp.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.davcamalv.filmApp.services.UserService;
import com.davcamalv.filmApp.services.WatsonService;

@RestController
@RequestMapping("/api/conversation")
@CrossOrigin
public class ConversationController{

	protected final Logger log = Logger.getLogger(ConversationController.class);
	
	@Autowired
	private WatsonService watsonService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/sendMessage")
	public MessageDTO list(@RequestBody MessageDTO message){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = userService.getByUsername(userDetails.getUsername()).get();
		log.info("POST /api/session/sendMessage");
		return watsonService.sendMessage(user.getId(), message);
	}
}
