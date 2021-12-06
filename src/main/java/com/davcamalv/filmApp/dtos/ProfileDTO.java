package com.davcamalv.filmApp.dtos;

import java.util.List;

public class ProfileDTO {

	private String name;
	
	private String username;
	
	private String email;
	
	private String avatar;
	
	private String birthDate;
	
	List<ProfileGenresDTO> genres;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public List<ProfileGenresDTO> getGenres() {
		return genres;
	}

	public void setGenres(List<ProfileGenresDTO> genres) {
		this.genres = genres;
	}

	public ProfileDTO(String name, String username, String email, String avatar, String birthDate,
			List<ProfileGenresDTO> genres) {
		super();
		this.name = name;
		this.username = username;
		this.email = email;
		this.avatar = avatar;
		this.birthDate = birthDate;
		this.genres = genres;
	}

	public ProfileDTO() {
		super();
	}
	
}
