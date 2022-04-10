package com.davcamalv.filmApp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.dtos.MediaContentDTO;
import com.davcamalv.filmApp.dtos.SearchDTO;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.services.JustWatchService;
import com.davcamalv.filmApp.services.MediaContentService;
import com.davcamalv.filmApp.services.PremiereService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class JustWatchServiceTest {

	@Autowired
	JustWatchService justWatchService;

	@Autowired
	MediaContentService mediaContentService;
	
	@Autowired
	PremiereService premiereService;

	@Test
	public void getSearchesTest() {
		List<SearchDTO> list = justWatchService.getSearches("Shrek");
		assertFalse(list.isEmpty());
		assertTrue(list.stream().anyMatch(
				x -> x.getImage() != null && x.getTitle() != null && x.getUrl() != null && x.getYear() != null));
	}

	@Test
	public void getFilteredSearchesTest() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("tipo", "Película");
		parameters.put("plataforma", "Amazon Prime Video");
		parameters.put("clasificacion_edad", "APTA");
		parameters.put("genero", "Comedia");
		parameters.put("fecha_inicial", "2018");
		parameters.put("fecha_final", "2019");

		List<SearchDTO> list = justWatchService.getFilteredSearches(parameters);
		assertFalse(list.isEmpty());
		assertTrue(list.stream().anyMatch(
				x -> x.getImage() != null && x.getTitle() != null && x.getUrl() != null));
	}
	
	@Test
	public void getMediaContent1Test() {
		mediaContentService.save(new MediaContent("Shrek", null, MediaType.MOVIE, "(2001)", "https://www.justwatch.com/es/pelicula/shrek", null, "https://images.justwatch.com/poster/175566090/s718", null, null, null, null));
		MediaContentDTO mediaContent = justWatchService.getMediaContent("https://www.justwatch.com/es/pelicula/shrek");
		assertNotEquals(mediaContent.getTitle(), null);
		assertNotEquals(mediaContent.getDescription(), null);
		assertNotEquals(mediaContent.getCreationDate(), null);
		assertNotEquals(mediaContent.getPoster(), null);
		assertNotEquals(mediaContent.getScore(), null);
		assertFalse(mediaContent.getBuy().isEmpty());
		assertFalse(mediaContent.getRent().isEmpty());
		assertFalse(mediaContent.getStream().isEmpty());
	}
	
	@Test
	public void getMediaContent2Test() {
		mediaContentService.save(new MediaContent("Shrek", null, MediaType.MOVIE, "(2001)", "https://www.justwatch.com/es/pelicula/shrek", null, "https://images.justwatch.com/poster/175566090/s718", null, null, null, null));
		MediaContentDTO mediaContent = justWatchService.getMediaContent("https://www.justwatch.com/es/pelicula/shrek");
		assertNotEquals(mediaContent.getTitle(), null);
		assertNotEquals(mediaContent.getDescription(), null);
		assertNotEquals(mediaContent.getCreationDate(), null);
		assertNotEquals(mediaContent.getPoster(), null);
		assertNotEquals(mediaContent.getScore(), null);
		assertFalse(mediaContent.getBuy().isEmpty());
		assertFalse(mediaContent.getRent().isEmpty());
		assertFalse(mediaContent.getStream().isEmpty());
		MediaContentDTO mediaContentWithSearchPerformed = justWatchService.getMediaContent("https://www.justwatch.com/es/pelicula/shrek");
		assertNotEquals(mediaContentWithSearchPerformed.getTitle(), null);
		assertNotEquals(mediaContentWithSearchPerformed.getDescription(), null);
		assertNotEquals(mediaContentWithSearchPerformed.getCreationDate(), null);
		assertNotEquals(mediaContentWithSearchPerformed.getPoster(), null);
		assertNotEquals(mediaContentWithSearchPerformed.getScore(), null);
		assertFalse(mediaContentWithSearchPerformed.getBuy().isEmpty());
		assertFalse(mediaContentWithSearchPerformed.getRent().isEmpty());
		assertFalse(mediaContentWithSearchPerformed.getStream().isEmpty());
	}
	
	@Test
	public void getMediaContent3Test() {
		mediaContentService.save(new MediaContent("NCIS: Nueva Orleans", null, MediaType.SERIE, null, "https://www.justwatch.com/es/serie/ncis-nueva-orleans", null, "https://images.justwatch.com/poster/237679839/s718", null, null, null, null));
		MediaContentDTO mediaContent = justWatchService.getMediaContent("https://www.justwatch.com/es/serie/ncis-nueva-orleans");
		assertNotEquals(mediaContent.getTitle(), null);
		assertNotEquals(mediaContent.getDescription(), null);
		assertNotEquals(mediaContent.getCreationDate(), null);
		assertNotEquals(mediaContent.getPoster(), null);
		assertNotEquals(mediaContent.getScore(), null);
	}
	
	@Test
	public void scrapePremieresTest() {
		int numberOfPremieresBeforeScrape = premiereService.findAll().size();
		justWatchService.scrapePremieres();
		int numberOfPremieresAfterScrape = premiereService.findAll().size();
		assertTrue(numberOfPremieresBeforeScrape < numberOfPremieresAfterScrape);
	}

}
