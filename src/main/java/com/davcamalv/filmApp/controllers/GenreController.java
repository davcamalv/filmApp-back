package com.davcamalv.filmApp.controllers;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.dtos.ProfileGenresDTO;
import com.davcamalv.filmApp.services.GenreService;

@RestController
@RequestMapping("/api/genre")
@CrossOrigin
public class GenreController{

	protected final Logger log = Logger.getLogger(GenreController.class);
	
	@Autowired
	private GenreService genreService;
	
	@GetMapping("/findAll")
	public List<ProfileGenresDTO> findAll(){
		log.info("POST /api/genre/findAll");
		return genreService.findAll();
	}
}
