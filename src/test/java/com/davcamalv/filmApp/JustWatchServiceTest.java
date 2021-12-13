package com.davcamalv.filmApp;

import static org.junit.Assert.assertFalse;
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

import com.davcamalv.filmApp.dtos.SearchDTO;
import com.davcamalv.filmApp.services.JustWatchService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class JustWatchServiceTest {

	@Autowired
	JustWatchService justWatchService;


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
}
