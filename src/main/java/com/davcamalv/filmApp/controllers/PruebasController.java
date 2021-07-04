package com.davcamalv.filmApp.controllers;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.services.JustWatchService;

@RestController
@RequestMapping("/api/prueba")
@CrossOrigin
public class PruebasController{

	protected final Logger log = Logger.getLogger(PruebasController.class);
	
	@Autowired
	private JustWatchService justWatchService;
	
	@GetMapping("/")
	public void list(){
		justWatchService.getPremieres();
	}
}
