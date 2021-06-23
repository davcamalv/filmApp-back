package com.davcamalv.filmApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.domain.UserPrincipal;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.getByUsername(username).get();
		return UserPrincipal.build(user);
	}
	
	
}
