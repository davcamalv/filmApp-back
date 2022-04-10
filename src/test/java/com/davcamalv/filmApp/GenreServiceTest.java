package com.davcamalv.filmApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.davcamalv.filmApp.dtos.ProfileGenresDTO;
import com.davcamalv.filmApp.services.GenreService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class GenreServiceTest {

	@Autowired
	GenreService genreService;
	
	@Test
	public void findAllTest() {
		List<ProfileGenresDTO> genres = genreService.findAll();
		List<String> genresExpected = Arrays.asList(new String[]{"Comedia", "Animación", "Fantasía", "Acción & Aventura", "Familia", "Drama"});
		assertEquals(genres.size(), 6);
		assertTrue(genres.stream().map(x -> x.getName()).collect(Collectors.toList()).containsAll(genresExpected));
	}
	
}
