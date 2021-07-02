package com.davcamalv.filmApp.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.davcamalv.filmApp.domain.Rol;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.NewUserDTO;
import com.davcamalv.filmApp.enums.RoleName;
import com.davcamalv.filmApp.repositories.UserRepository;
import com.davcamalv.filmApp.utils.Utils;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RolService rolService;
	
	public Optional<User> findOne(Long userId) {
		return userRepository.findById(userId);
	}
	
	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public User createNewUser(NewUserDTO newUserDTO) {
		validateNewUserDTO(newUserDTO);
		User user = new User(newUserDTO.getName(), newUserDTO.getUsername(), 
				passwordEncoder.encode(newUserDTO.getPassword()), newUserDTO.getEmail());
		Set<Rol> roles = new HashSet<>();
		roles.add(rolService.getByRoleName(RoleName.ROLE_USER).get());
		if(newUserDTO.getRoles().contains("admin")) {
			roles.add(rolService.getByRoleName(RoleName.ROLE_ADMIN).get());
		}
		user.setRoles(roles);
		return save(user);
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	private void validateNewUserDTO(NewUserDTO newUserDTO) {
		if(newUserDTO.getName() == "" || newUserDTO.getPassword() == "" ||  newUserDTO.getUsername() == "") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Params are required");	
		}
		
		if(existsByUsername(newUserDTO.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username alredy exists");	
		} 
		
		if(!Utils.isValidEmail(newUserDTO.getEmail())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");	
		}
	}

	public User getByUserLogged() {
		return getByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
	}
}
