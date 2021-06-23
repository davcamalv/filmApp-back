package com.davcamalv.filmApp.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.repositories.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public Optional<User> findOne(Long userId) {
		return userRepository.findById(userId);
	}
	
	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
}
