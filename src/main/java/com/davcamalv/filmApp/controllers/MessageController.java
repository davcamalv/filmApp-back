package com.davcamalv.filmApp.controllers;

import java.util.List;

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
import com.davcamalv.filmApp.dtos.PaginationDTO;
import com.davcamalv.filmApp.services.MessageService;
import com.davcamalv.filmApp.services.UserService;

@RestController
@RequestMapping("/api/message")
@CrossOrigin
public class MessageController{

	protected final Logger log = Logger.getLogger(MessageController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageService messageService;
	
	@PostMapping("/findByUser")
	public List<MessageDTO> list(@RequestBody PaginationDTO paginationDTO){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = userService.getByUsername(userDetails.getUsername()).get();
		log.info("POST /api/message/findByUser");
		return messageService.findMessagesByUser(paginationDTO.getPageNumber(), paginationDTO.getPageSize(), user);
	}

}
