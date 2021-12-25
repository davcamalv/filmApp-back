package com.davcamalv.filmApp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.dtos.CreditsDTO;
import com.davcamalv.filmApp.dtos.PersonDTO;
import com.davcamalv.filmApp.dtos.ReviewDTO;
import com.davcamalv.filmApp.services.JustWatchService;
import com.davcamalv.filmApp.services.MediaContentService;
import com.davcamalv.filmApp.services.TMDBService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({ "/test.sql" })
public class TMDBServiceTest {

	@Autowired
	JustWatchService justWatchService;

	@Autowired
	MediaContentService mediaContentService;

	@Autowired
	TMDBService TMDBService;

	@Test
	public void getTrailerTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		String url = TMDBService.getTrailer(mediaContent);
		assertFalse(url == null || "".equals(url));
	}

	@Test
	public void getCastByMediaContentTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		CreditsDTO credits = TMDBService.getCastByMediaContent(mediaContent);
		assertFalse(credits == null);
		assertFalse(credits.getCast().isEmpty());
		assertFalse(credits.getCrew().isEmpty());
	}

	@Test
	public void getDirectorTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		CreditsDTO credits = TMDBService.getCastByMediaContent(mediaContent);
		assertFalse(credits == null);
		assertFalse(credits.getCrew().isEmpty());
		PersonDTO director = TMDBService.getDirector(credits.getCrew());
		assertFalse(director == null);
		assertFalse(director.getName() == null || "".equals(director.getName()));
	}

	@Test
	public void searchPeopleTest() throws UnsupportedEncodingException {
		List<PersonDTO> personList = TMDBService.searchPeople("rocky");
		assertFalse(personList.isEmpty());
	}

	@Test
	public void getReviewsByMediaContentTest() {
		MediaContent mediaContent = mediaContentService.findById(9174l);
		List<ReviewDTO> reviews = TMDBService.getReviewsByMediaContent(mediaContent).getResults();
		assertFalse(reviews.isEmpty());
		assertTrue(reviews.get(0).getAuthor() != null && !"".equals(reviews.get(0).getAuthor())
				&& reviews.get(0).getContent() != null && !"".equals(reviews.get(0).getContent()));

	}

}
