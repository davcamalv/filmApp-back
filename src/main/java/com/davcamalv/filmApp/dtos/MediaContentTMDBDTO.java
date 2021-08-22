package com.davcamalv.filmApp.dtos;

import java.util.List;

public class MediaContentTMDBDTO {
	
	private List<TMDBResultDTO> movie_results;
	
	private List<TMDBResultDTO> tv_results;


	public List<TMDBResultDTO> getMovie_results() {
		return movie_results;
	}

	public void setMovie_results(List<TMDBResultDTO> movie_results) {
		this.movie_results = movie_results;
	}

	public List<TMDBResultDTO> getTv_results() {
		return tv_results;
	}

	public void setTv_results(List<TMDBResultDTO> tv_results) {
		this.tv_results = tv_results;
	}

	public MediaContentTMDBDTO(List<TMDBResultDTO> movie_results, List<TMDBResultDTO> tv_results) {
		super();
		this.movie_results = movie_results;
		this.tv_results = tv_results;
	}

	public MediaContentTMDBDTO() {
		super();
	}
	
}
