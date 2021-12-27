package com.davcamalv.filmApp.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.dtos.AuthorDetailsDTO;
import com.davcamalv.filmApp.dtos.ReviewDTO;
import com.davcamalv.filmApp.repositories.ReviewRepository;

@Service
@Transactional
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	public Review save(Review review) {
		return reviewRepository.save(review);
	}

	public List<ReviewDTO> findByMediaContent(MediaContent mediaContent) {
		List<Review> reviews = reviewRepository.findByMediaContent(mediaContent);
		return reviews.stream()
				.map(x -> new ReviewDTO(new AuthorDetailsDTO(x.getUser().getUsername(), x.getUser().getAvatar()),
						x.getContent(), x.getCreatedAt(), x.getRating()))
				.collect(Collectors.toList());
	}
}
