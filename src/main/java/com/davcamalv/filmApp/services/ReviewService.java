package com.davcamalv.filmApp.services;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.AuthorDetailsDTO;
import com.davcamalv.filmApp.dtos.MediaContentReviewDTO;
import com.davcamalv.filmApp.dtos.ReviewDTO;
import com.davcamalv.filmApp.dtos.ReviewProfileDTO;
import com.davcamalv.filmApp.repositories.ReviewRepository;

@Service
@Transactional
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	public Review save(Review review) {
		if(Boolean.TRUE.equals(review.getDraft())) {
			deleteByMediaContentAndUserAndDraft(review.getMediaContent(), review.getUser(), true);
		}
		return reviewRepository.save(review);
	}
	
	public void deleteByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, Boolean draft) {
		reviewRepository.deleteByMediaContentAndUserAndDraft(mediaContent, user, draft);
	}

	public List<ReviewDTO> findByMediaContent(MediaContent mediaContent) {
		List<Review> reviews = reviewRepository.findByMediaContentAndDraft(mediaContent, false);
		return reviews.stream()
				.map(x -> new ReviewDTO(new AuthorDetailsDTO(x.getUser().getUsername(), x.getUser().getAvatar()),
						x.getContent(), x.getCreatedAt(), x.getRating()))
				.collect(Collectors.toList());
	}

	public List<ReviewProfileDTO> findByUser(User user, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
		List<Review> reviews = reviewRepository.findByUserAndDraft(user, false, pageable);
		return reviews.stream()
				.map(x -> new ReviewProfileDTO(x.getId(),
						new MediaContentReviewDTO(x.getMediaContent().getTitle(),
								"MOVIE".equals(x.getMediaContent().getMediaType().name()) ? "Película" : "Serie",
								x.getMediaContent().getCreationDate(), x.getMediaContent().getPoster()),
						x.getContent(), new SimpleDateFormat("dd/MM/yyyy").format(x.getCreatedAt()), x.getRating()))
				.collect(Collectors.toList());
	}

	public void delete(Long id) {
		reviewRepository.deleteById(id);
	}

	public boolean existsByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, Boolean draft) {
		return reviewRepository.existsByMediaContentAndUserAndDraft(mediaContent, user, draft);
	}

	public void updateDraft(MediaContent mediaContent, User user) {
		Review review;
		List<Review> reviews = findByMediaContentAndUserAndDraft(mediaContent, user, true);
		if(!reviews.isEmpty()) {
			deleteByMediaContentAndUserAndDraft(mediaContent, user, false);
			review = reviews.get(0);
			review.setDraft(false);
			save(review);
		}
	}

	public List<Review> findByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, Boolean draft) {
		return reviewRepository.findByMediaContentAndUserAndDraft(mediaContent, user, draft);
	}

}
