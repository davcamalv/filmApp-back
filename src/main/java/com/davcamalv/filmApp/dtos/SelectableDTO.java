package com.davcamalv.filmApp.dtos;

import java.util.List;

public class SelectableDTO {
	
	private Long id;
	
	private List<OptionDTO> options;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<OptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<OptionDTO> options) {
		this.options = options;
	}

	public SelectableDTO(Long id, List<OptionDTO> options) {
		super();
		this.id = id;
		this.options = options;
	}

	public SelectableDTO() {
		super();
	}
	
}
