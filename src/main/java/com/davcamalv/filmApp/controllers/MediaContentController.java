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

import com.davcamalv.filmApp.dtos.PaginationDTO;
import com.davcamalv.filmApp.dtos.WatchListDTO;
import com.davcamalv.filmApp.services.UserService;

@RestController
@RequestMapping("/api/mediaContent")
@CrossOrigin
public class MediaContentController{

	protected final Logger log = Logger.getLogger(MediaContentController.class);
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/findByUser")
	public List<WatchListDTO> findByUser(@RequestBody PaginationDTO paginationDTO){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		log.info("POST /api/mediaContent/findByUser");
		return userService.getToWatchListByUsername(paginationDTO.getPageNumber(), paginationDTO.getPageSize(), userDetails.getUsername());
	}

}
