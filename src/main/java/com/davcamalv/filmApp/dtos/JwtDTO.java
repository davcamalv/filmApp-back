package com.davcamalv.filmApp.dtos;

public class JwtDTO {

	private String token;
	
	public JwtDTO(String token) {
		this.token = token;
	}
	
	public JwtDTO() {
		super();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
