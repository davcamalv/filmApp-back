package com.davcamalv.filmApp.dtos;

public class MediaContentReviewDTO {
	
	private String title;
    
	private String mediaType;
	
	private String creationDate;
	
	private String poster;

	public MediaContentReviewDTO() {
		super();
	}

	public MediaContentReviewDTO(String title, String mediaType, String creationDate, String poster) {
		super();
		this.title = title;
		this.mediaType = mediaType;
		this.creationDate = creationDate;
		this.poster = poster;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

}
