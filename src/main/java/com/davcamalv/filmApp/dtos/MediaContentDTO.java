package com.davcamalv.filmApp.dtos;

import java.util.List;

public class MediaContentDTO {
	
	private String title;
	
	private String description;
    
	private String mediaType;
	
	private String creationDate;
	
	private String poster;
	
	private String score;
	
	private List<PlatformWithPriceDTO> rent;
	
	private List<PlatformWithPriceDTO> stream;

	private List<PlatformWithPriceDTO> buy;

	public MediaContentDTO() {
		super();
	}

	public MediaContentDTO(String title, String description, String mediaType, String creationDate, String poster,
			String score, List<PlatformWithPriceDTO> rent, List<PlatformWithPriceDTO> stream,
			List<PlatformWithPriceDTO> buy) {
		super();
		this.title = title;
		this.description = description;
		this.mediaType = mediaType;
		this.creationDate = creationDate;
		this.poster = poster;
		this.score = score;
		this.rent = rent;
		this.stream = stream;
		this.buy = buy;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public List<PlatformWithPriceDTO> getRent() {
		return rent;
	}

	public void setRent(List<PlatformWithPriceDTO> rent) {
		this.rent = rent;
	}

	public List<PlatformWithPriceDTO> getStream() {
		return stream;
	}

	public void setStream(List<PlatformWithPriceDTO> stream) {
		this.stream = stream;
	}

	public List<PlatformWithPriceDTO> getBuy() {
		return buy;
	}

	public void setBuy(List<PlatformWithPriceDTO> buy) {
		this.buy = buy;
	}

}
