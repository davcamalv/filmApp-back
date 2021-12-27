package com.davcamalv.filmApp.dtos;

public class AuthorDetailsDTO {
	
	private String username;
	
	private String avatar_path;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar_path() {
		return avatar_path;
	}

	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}

	public AuthorDetailsDTO(String username, String avatar_path) {
		super();
		this.username = username;
		this.avatar_path = avatar_path;
	}

	public AuthorDetailsDTO() {
		super();
	}
	
}
