package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.repositories.ReviewRepository;

@Service
@Transactional
public class ReviewService {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	public Review save(Review review) {
		return reviewRepository.save(review);
	}
}
