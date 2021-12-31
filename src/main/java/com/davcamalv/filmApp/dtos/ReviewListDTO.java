package com.davcamalv.filmApp.dtos;

import java.util.List;

public class ReviewListDTO {

	private List<ReviewDTO> results;

	public ReviewListDTO() {
		super();
	}

	public ReviewListDTO(List<ReviewDTO> results) {
		super();
		this.results = results;
	}

	public List<ReviewDTO> getResults() {
		return results;
	}

	public void setResults(List<ReviewDTO> results) {
		this.results = results;
	}
}
