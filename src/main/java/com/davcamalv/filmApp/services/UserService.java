package com.davcamalv.filmApp.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Rol;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.NewUserDTO;
import com.davcamalv.filmApp.dtos.ProfileDTO;
import com.davcamalv.filmApp.dtos.ProfileGenresDTO;
import com.davcamalv.filmApp.dtos.WatchListDTO;
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
		if (newUserDTO.getRoles().contains("admin")) {
			roles.add(rolService.getByRoleName(RoleName.ROLE_ADMIN).get());
		}
		user.setRoles(roles);
		return save(user);
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	private void validateNewUserDTO(NewUserDTO newUserDTO) {
		if (newUserDTO.getName() == "" || newUserDTO.getPassword() == "" || newUserDTO.getUsername() == "") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Params are required");
		}

		if (existsByUsername(newUserDTO.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username alredy exists");
		}

		if (!Utils.isValidEmail(newUserDTO.getEmail())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
		}
	}

	public User getByUserLogged() {
		return getByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();
	}

	public List<WatchListDTO> getToWatchListByUsername(String username) {
		List<MediaContent> mediaContentList = userRepository.getToWatchListByUsername(username);
		return mediaContentList.stream().map(x -> new WatchListDTO(x.getId(), x.getTitle(), x.getScore(),
				x.getCreationDate(), x.getMediaType().name(), x.getPoster())).collect(Collectors.toList());
	}

	public ProfileDTO getProfile() {
		User user = getByUserLogged();
		String pattern = "dd/MM/yyyy";
		String birthDate = null;
		DateFormat df = new SimpleDateFormat(pattern);
		if(user.getBirthDate() != null) {
			birthDate = df.format(user.getBirthDate());
		}
		List<ProfileGenresDTO> genres = user.getGenres().stream().map(x -> new ProfileGenresDTO(x.getId(), x.getName()))
				.collect(Collectors.toList());
		return new ProfileDTO(user.getName(), user.getUsername(), user.getEmail(), user.getAvatar(),
				birthDate, genres);
	}

}
