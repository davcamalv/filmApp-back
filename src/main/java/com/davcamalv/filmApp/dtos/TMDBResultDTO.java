package com.davcamalv.filmApp.dtos;

import java.util.List;

public class TMDBResultDTO {
	
	private Integer id;
	
	private List<Integer> genre_ids;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Integer> getGenre_ids() {
		return genre_ids;
	}

	public void setGenre_ids(List<Integer> genre_ids) {
		this.genre_ids = genre_ids;
	}

	public TMDBResultDTO(Integer id, List<Integer> genre_ids) {
		super();
		this.id = id;
		this.genre_ids = genre_ids;
	}

	public TMDBResultDTO() {
		super();
	}
}
