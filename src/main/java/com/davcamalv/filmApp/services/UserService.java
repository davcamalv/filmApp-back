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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.davcamalv.filmApp.domain.Genre;
import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Rol;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.NewUserDTO;
import com.davcamalv.filmApp.dtos.ProfileDTO;
import com.davcamalv.filmApp.dtos.ProfileDetailsDTO;
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
	
	@Autowired
	private GenreService genreService;

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
		if (newUserDTO.getRoles() != null && newUserDTO.getRoles().contains("admin")) {
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

	public List<WatchListDTO> getToWatchListByUsername(int pageNumber, int pageSize,  String username) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
		List<MediaContent> mediaContentList = userRepository.getToWatchListByUsername(pageable, username);
		return mediaContentList.stream().map(x -> new WatchListDTO(x.getId(), x.getTitle(), x.getScore(),
				x.getCreationDate(), "MOVIE".equals(x.getMediaType().name())?"Película":"Serie", x.getPoster())).collect(Collectors.toList());
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
	
	public ProfileDTO addGenresToPrincipal(List<Long> ids) {
		List<Genre> genres = genreService.findByIds(ids);
		User user = getByUserLogged();
		user.setGenres(genres);
		userRepository.saveAndFlush(user);
		return getProfile();
	}

	public void deleteElementMediaContentList(Long id) {
		User user = getByUserLogged();
		List<MediaContent> toWatchList = user.getToWatchList();
		toWatchList = toWatchList.stream().filter(x -> !x.getId().equals(id)).collect(Collectors.toList());
		user.setToWatchList(toWatchList);
		userRepository.saveAndFlush(user);
	}

	public void changeAvatar(String avatar) {
		User user = getByUserLogged();
		user.setAvatar(avatar);
		userRepository.saveAndFlush(user);
	}

	public ProfileDTO saveDetails(ProfileDetailsDTO profileDetailsDTO) {
		User user = getByUserLogged();
		user.setBirthDate(profileDetailsDTO.getBirthDate());
		user.setName(profileDetailsDTO.getName());
		user.setEmail(profileDetailsDTO.getEmail());
		userRepository.saveAndFlush(user);
		return getProfile();
	}

	public boolean existsOnToWatchList(Long id) {
		return userRepository.existsOnToWatchList(getByUserLogged().getId(), id);
	}

	public void addToWatchList(MediaContent mediaContent) {
		User user = getByUserLogged();
		List<MediaContent> toWatchList = user.getToWatchList();
		toWatchList.add(mediaContent);
		user.setToWatchList(toWatchList);
		userRepository.saveAndFlush(user);
	}
	
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
