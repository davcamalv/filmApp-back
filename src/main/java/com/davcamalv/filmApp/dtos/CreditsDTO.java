package com.davcamalv.filmApp.dtos;

import java.util.List;

public class CreditsDTO {

	private List<PersonDTO> cast;

	public List<PersonDTO> getCast() {
		return cast;
	}

	public void setCast(List<PersonDTO> cast) {
		this.cast = cast;
	}

	public CreditsDTO(List<PersonDTO> cast) {
		super();
		this.cast = cast;
	}

	public CreditsDTO() {
		super();
	}

}
