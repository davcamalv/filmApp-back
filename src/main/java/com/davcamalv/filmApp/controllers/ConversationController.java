package com.davcamalv.filmApp.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.dtos.MessageDto;
import com.davcamalv.filmApp.services.WatsonService;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController{

	protected final Logger log = Logger.getLogger(ConversationController.class);
	
	@Autowired
	private WatsonService watsonService;
	
	@PostMapping("/sendMessage/{idUsuario}")
	public String list(@PathVariable Long idUsuario, @RequestBody MessageDto message){
		log.info("POST /api/session/sendMessage/" + idUsuario);
		return watsonService.sendMessage(idUsuario, message);
	}
}
