package com.davcamalv.filmApp.dtos;

public class PlatformWithPriceDTO {
	
	private String name;
	
	private String logo;
	
	private String cost;

	public PlatformWithPriceDTO() {
		super();
	}

	public PlatformWithPriceDTO(String name, String logo, String cost) {
		super();
		this.name = name;
		this.logo = logo;
		this.cost = cost;
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
}
