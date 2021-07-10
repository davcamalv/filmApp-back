package com.davcamalv.filmApp.dtos;

public class SearchDTO {
	
	private String url;
	
	private String title;
	
	private String year;

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

	public SearchDTO(String url, String title, String year) {
		super();
		this.url = url;
		this.title = title;
		this.year = year;
	}

	public SearchDTO() {
		super();
	}
	
}
