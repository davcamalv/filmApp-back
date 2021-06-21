package com.davcamalv.filmApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User findOne(Long userId) {
		return userRepository.findById(userId).orElseGet(null);
	}
}
