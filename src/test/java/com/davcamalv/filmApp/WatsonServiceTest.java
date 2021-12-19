package com.davcamalv.filmApp;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.davcamalv.filmApp.enums.SenderType;
import com.davcamalv.filmApp.services.UserService;
import com.davcamalv.filmApp.services.WatsonService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class WatsonServiceTest {

	@Autowired
	WatsonService watsonService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserDetailsService userDetailsService;
	@Test
	public void sendMessage1Test() {
		MessageDTO message = new MessageDTO("hola", SenderType.user.name(), false, null);
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
		User user = userService.getByUserLogged();
		MessageDTO response = watsonService.sendMessage(user.getId(), message);
		assertEquals(response.getSender(), SenderType.server.name());
		assertNotEquals(response.getMessage(), 0);
		assertNotEquals(response.getMessage(), "Disculpe, actualmente no tengo implementada esa funcionalidad");
	}
	
	@Test
	public void sendMessage2Test() {
		MessageDTO message = new MessageDTO("dime las funciones", SenderType.user.name(), false, null);
		UserDetails userDetails = userDetailsService.loadUserByUsername ("admin");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
		User user = userService.getByUserLogged();
		MessageDTO response = watsonService.sendMessage(user.getId(), message);
		assertEquals(response.getSender(), SenderType.server.name());
		assertNotEquals(response.getMessage(), 0);
		assertNotEquals(response.getMessage(), "Disculpe, actualmente no tengo implementada esa funcionalidad");
		assertTrue(response.getSpecialKeyboard());
	}
	
}
