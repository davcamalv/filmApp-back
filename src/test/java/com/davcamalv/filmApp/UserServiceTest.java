package com.davcamalv.filmApp;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.dtos.NewUserDTO;
import com.davcamalv.filmApp.dtos.ProfileDTO;
import com.davcamalv.filmApp.dtos.ProfileDetailsDTO;
import com.davcamalv.filmApp.dtos.WatchListDTO;
import com.davcamalv.filmApp.services.MediaContentService;
import com.davcamalv.filmApp.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class UserServiceTest {

	@Autowired
	UserService userService;
	
	@Autowired
	MediaContentService mediaContentService;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Test
	public void toWatchListTest() {
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		List<WatchListDTO> toWatchList = userService.getToWatchListByUsername(0, 10, "admin");
		assertTrue(toWatchList.isEmpty());
		assertFalse(userService.existsOnToWatchList(9174l));

		MediaContent mediaContent = mediaContentService.findById(9174l);
		userService.addToWatchList(mediaContent);
		toWatchList = userService.getToWatchListByUsername(0, 10, "admin");
		assertEquals(toWatchList.size(), 1);
		assertEquals(toWatchList.get(0).getId(), mediaContent.getId());
		assertTrue(userService.existsOnToWatchList(9174l));

		userService.deleteElementMediaContentList(9174l);
		toWatchList = userService.getToWatchListByUsername(0, 10, "admin");
		assertTrue(toWatchList.isEmpty());
		assertFalse(userService.existsOnToWatchList(9174l));
	}
	
	@Test
	public void profileTest() {
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		ProfileDTO profile = userService.getProfile();
		assertEquals(profile.getName(), "admin");
		assertEquals(profile.getBirthDate(), "08/02/1998");
		assertEquals(profile.getAvatar(), "/assets/avatars/6.png");
		assertEquals(profile.getEmail(), "dcamalv@gmail.com");
		assertEquals(profile.getUsername(), "admin");
		assertEquals(profile.getGenres().size(), 1);
		assertEquals(profile.getGenres().get(0).getName(), "Comedia");
		Date date = new GregorianCalendar(2021, Calendar.NOVEMBER, 12).getTime();
		ProfileDetailsDTO profileDetailsDTO = new ProfileDetailsDTO("newName", "mail@gmail.com", date);
		ProfileDTO profileAfterUpdate = userService.saveDetails(profileDetailsDTO);
		assertTrue(profileAfterUpdate.getName().equals("newName"));
		assertTrue(profileAfterUpdate.getBirthDate().equals("12/11/2021"));
		assertTrue(profileAfterUpdate.getEmail().equals("mail@gmail.com"));

	}
	
	@Test
	public void addGenresToPrincipalTest() {
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		ProfileDTO profileBeforeUpdate = userService.getProfile();
		List<Long> genreIds = Arrays.asList(new Long[]{2l,3l});
		ProfileDTO profileAfterUpdate = userService.addGenresToPrincipal(genreIds);
		List<String> genresBeforeUpdate = Arrays.asList(new String[]{"Comedia"});
		List<String> genresAfterUpdate = Arrays.asList(new String[]{"Animación", "Fantasía"});

		assertTrue(profileBeforeUpdate.getGenres().stream().map(x -> x.getName()).collect(Collectors.toList()).containsAll(genresBeforeUpdate) && profileBeforeUpdate.getGenres().size() == 1);
		assertTrue(profileAfterUpdate.getGenres().stream().map(x -> x.getName()).collect(Collectors.toList()).containsAll(genresAfterUpdate) && profileAfterUpdate.getGenres().size() == 2);
	}
	
	
	@Test
	public void changeAvatarTest() {
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		ProfileDTO profile = userService.getProfile();
		assertTrue(profile.getAvatar().equals("/assets/avatars/6.png"));
		userService.changeAvatar("/assets/avatars/7.png");
		ProfileDTO profileAfterUpdate = userService.getProfile();
		assertTrue(profileAfterUpdate.getAvatar().equals("/assets/avatars/7.png"));
	}
	
	@Test
	public void createNewUserTest1() {
		NewUserDTO newUserDTO = new NewUserDTO("newUser", "newUser", "mail2@gmail.com", "password", new HashSet<String>(Arrays.asList(new String[] {"admin"})));
		assertEquals(userService.findAll().size(), 1);
		userService.createNewUser(newUserDTO);
		assertEquals(userService.findAll().size(), 2);
	}
	
	@Test
	public void createNewUserTest2() {
		NewUserDTO newUserDTO = new NewUserDTO("", "newUser", "mail2@gmail.com", "password", new HashSet<String>(Arrays.asList(new String[] {"admin"})));
		assertEquals(userService.findAll().size(), 1);
		ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> {
			userService.createNewUser(newUserDTO);
		}, "Params are required expected");
		
		assertEquals("400 BAD_REQUEST \"Params are required\"", thrown.getMessage());
	}
	
	@Test
	public void createNewUserTest3() {
		NewUserDTO newUserDTO = new NewUserDTO("admin", "admin", "mail2@gmail.com", "password", new HashSet<String>(Arrays.asList(new String[] {"admin"})));
		assertEquals(userService.findAll().size(), 1);
		ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> {
			userService.createNewUser(newUserDTO);
		}, "Username alredy exists expected");
		
		assertEquals("400 BAD_REQUEST \"Username alredy exists\"", thrown.getMessage());
	}
	
	@Test
	public void createNewUserTest4() {
		NewUserDTO newUserDTO = new NewUserDTO("newUser", "newUser", "mail2", "password", new HashSet<String>(Arrays.asList(new String[] {"admin"})));
		assertEquals(userService.findAll().size(), 1);
		ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class, () -> {
			userService.createNewUser(newUserDTO);
		}, "Invalid email expected");
		
		assertEquals("400 BAD_REQUEST \"Invalid email\"", thrown.getMessage());
	}
}
