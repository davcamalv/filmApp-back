package com.davcamalv.filmApp.dtos;

public class SearchDTO {
	
	private String url;
	
	private String title;
	
	private String year;
	
	private String image;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public SearchDTO(String url, String title, String year, String image) {
		super();
		this.url = url;
		this.title = title;
		this.year = year;
		this.image = image;
	}

	public SearchDTO() {
		super();
	}
	
}
