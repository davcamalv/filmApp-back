package com.davcamalv.filmApp.dtos;

public class PersonDTO {
	
	private String name;
	
	private String character;
	
	private String profile_path;
	
	private Integer order;
	
	private String job;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public String getProfile_path() {
		return profile_path;
	}

	public void setProfile_path(String profile_path) {
		this.profile_path = profile_path;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public PersonDTO(String name, String character, String profile_path, Integer order, String job) {
		super();
		this.name = name;
		this.character = character;
		this.profile_path = profile_path;
		this.order = order;
		this.job = job;
	}

	public PersonDTO() {
		super();
	}
	
	
}
