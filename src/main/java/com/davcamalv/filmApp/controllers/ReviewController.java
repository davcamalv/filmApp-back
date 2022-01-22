package com.davcamalv.filmApp.controllers;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.PaginationDTO;
import com.davcamalv.filmApp.dtos.ReviewProfileDTO;
import com.davcamalv.filmApp.services.ReviewService;
import com.davcamalv.filmApp.services.UserService;

@RestController
@RequestMapping("/api/review")
@CrossOrigin
public class ReviewController{

	protected final Logger log = Logger.getLogger(ReviewController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/findByUser")
	public List<ReviewProfileDTO> findByPrincipal(@RequestBody PaginationDTO paginationDTO){
		User user = userService.getByUserLogged();
		log.info("POST /api/review/findByUser");
		return reviewService.findByUser(user, paginationDTO.getPageNumber(), paginationDTO.getPageSize());
	}
	
	@PostMapping("/delete")
	public void delete(@RequestBody Long id){
		log.info("POST /api/review/delete");
		reviewService.delete(id);
	}
}
