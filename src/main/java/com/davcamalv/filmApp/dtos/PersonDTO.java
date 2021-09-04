package com.davcamalv.filmApp.dtos;

public class PersonDTO {
	
	private Integer id;
	
	private String name;
	
	private String character;
	
	private String profile_path;
	
	private Integer order;
	
	private String job;
	
	private String birthday;
	
	private String deathday;
	
	private String biography;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getDeathday() {
		return deathday;
	}

	public void setDeathday(String deathday) {
		this.deathday = deathday;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public PersonDTO(Integer id, String name, String character, String profile_path, Integer order, String job,
			String birthday, String deathday, String biography) {
		super();
		this.id = id;
		this.name = name;
		this.character = character;
		this.profile_path = profile_path;
		this.order = order;
		this.job = job;
		this.birthday = birthday;
		this.deathday = deathday;
		this.biography = biography;
	}

	public PersonDTO() {
		super();
	}
	
	
}
