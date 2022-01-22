package com.davcamalv.filmApp.dtos;

public class OptionDTO {
	
	private String label;
	
	private String text;
	
	private String image;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public OptionDTO(String label, String text, String image) {
		super();
		this.label = label;
		this.text = text;
		this.image = image;
	}

	public OptionDTO() {
		super();
	}
	
}
