package com.davcamalv.filmApp.dtos;

public class OptionDTO {
	
	private String label;
	
	private String text;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OptionDTO(String label, String text) {
		super();
		this.label = label;
		this.text = text;
	}

	public OptionDTO() {
		super();
	}
	
}
