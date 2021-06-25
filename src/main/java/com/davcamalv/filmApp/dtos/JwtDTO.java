package com.davcamalv.filmApp.dtos;

public class JwtDTO {

	private String token;
	
	public JwtDTO(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
