package com.davcamalv.filmApp;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.ReviewDTO;
import com.davcamalv.filmApp.dtos.ReviewProfileDTO;
import com.davcamalv.filmApp.services.MediaContentService;
import com.davcamalv.filmApp.services.ReviewService;
import com.davcamalv.filmApp.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class ReviewServiceTest {

	@Autowired
	ReviewService reviewService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	MediaContentService mediaContentService;
	
	@Test
	public void findByUserTest() {
		Optional<User> user = userService.findOne(7l);
		if(user.isPresent()) {
			List<ReviewProfileDTO> reviews = reviewService.findByUser(user.get(), 0, 10);
			assertEquals(reviews.size(), 1);
			assertEquals(reviews.get(0).getMediaContent().getTitle(), "El club de la lucha");
			assertEquals(reviews.get(0).getContent(), "review 1");
			assertEquals(reviews.get(0).getRating(), 4);
			assertEquals(reviews.get(0).getCreatedAt(), "12/12/2021");
		}
	}
	
	@Test
	public void findByMediaContentTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		List<ReviewDTO> reviews = reviewService.findByMediaContent(mediaContent);
		assertEquals(reviews.size(), 1);
		assertEquals(reviews.get(0).getAuthor_details().getUsername(), "admin");
		assertEquals(reviews.get(0).getContent(), "review 1");
		assertEquals(reviews.get(0).getRating(), 4);
		assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(reviews.get(0).getCreated_at()), "12/12/2021");
	}
	
	@Test
	public void updateDraftTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		Optional<User> user = userService.findOne(7l);
		if(user.isPresent()) {
			
			List<Review> nodraftReviews = reviewService.findByMediaContentAndUserAndDraft(mediaContent, user.get(), false);
			List<Review> draftReviews = reviewService.findByMediaContentAndUserAndDraft(mediaContent, user.get(), true);

			assertEquals(nodraftReviews.size(), 1);
			assertEquals(nodraftReviews.get(0).getId(), 1l);
			assertEquals(draftReviews.size(), 1);
			assertEquals(draftReviews.get(0).getId(), 2l);

			reviewService.updateDraft(mediaContent, user.get());
			
			nodraftReviews = reviewService.findByMediaContentAndUserAndDraft(mediaContent, user.get(), false);
			draftReviews = reviewService.findByMediaContentAndUserAndDraft(mediaContent, user.get(), true);

			assertEquals(nodraftReviews.size(), 1);
			assertEquals(nodraftReviews.get(0).getId(), 2l);
			assertTrue(draftReviews.isEmpty());			
		}
	}
	
}
