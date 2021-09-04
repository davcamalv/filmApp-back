package com.davcamalv.filmApp.dtos;

import java.util.List;

public class CreditsDTO {

	private List<PersonDTO> cast;
	
	private List<PersonDTO> crew;


	public List<PersonDTO> getCast() {
		return cast;
	}

	public void setCast(List<PersonDTO> cast) {
		this.cast = cast;
	}

	public List<PersonDTO> getCrew() {
		return crew;
	}

	public void setCrew(List<PersonDTO> crew) {
		this.crew = crew;
	}

	public CreditsDTO(List<PersonDTO> cast, List<PersonDTO> crew) {
		super();
		this.cast = cast;
		this.crew = crew;
	}

	public CreditsDTO() {
		super();
	}

}
