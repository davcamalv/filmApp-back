package com.davcamalv.filmApp.dtos;

public class TrailerResultDTO {
	
	private String site;
	
	private String type;
	
	private String key;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public TrailerResultDTO(String site, String type, String key) {
		super();
		this.site = site;
		this.type = type;
		this.key = key;
	}

	public TrailerResultDTO() {
		super();
	}
	
}
