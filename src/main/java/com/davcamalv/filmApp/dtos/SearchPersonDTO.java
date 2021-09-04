package com.davcamalv.filmApp.dtos;

import java.util.List;

public class SearchPersonDTO {
	
	private List<PersonDTO> results;

	public List<PersonDTO> getResults() {
		return results;
	}

	public void setResults(List<PersonDTO> results) {
		this.results = results;
	}

	public SearchPersonDTO(List<PersonDTO> results) {
		super();
		this.results = results;
	}

	public SearchPersonDTO() {
		super();
	}

}
