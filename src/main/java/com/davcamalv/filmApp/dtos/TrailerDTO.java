package com.davcamalv.filmApp.dtos;

import java.util.List;

public class TrailerDTO {
	
	private List<TrailerResultDTO> results;

	public List<TrailerResultDTO> getResults() {
		return results;
	}

	public void setResults(List<TrailerResultDTO> results) {
		this.results = results;
	}

	public TrailerDTO(List<TrailerResultDTO> results) {
		super();
		this.results = results;
	}

	public TrailerDTO() {
		super();
	}

}
