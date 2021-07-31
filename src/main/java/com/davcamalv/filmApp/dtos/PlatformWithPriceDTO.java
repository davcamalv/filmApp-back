package com.davcamalv.filmApp.dtos;

public class PlatformWithPriceDTO {
	
	private String name;
	
	private String logo;
	
	private String cost;
	
	private String url;

	public PlatformWithPriceDTO() {
		super();
	}

	public PlatformWithPriceDTO(String name, String logo, String cost, String url) {
		super();
		this.name = name;
		this.logo = logo;
		this.cost = cost;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
