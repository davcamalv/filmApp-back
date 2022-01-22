package com.davcamalv.filmApp.dtos;

public class ReviewProfileDTO {
	
	private Long id;
	
	private MediaContentReviewDTO mediaContent;
		
	private String content;
	
	private String createdAt;
	
	private Integer rating;

	public ReviewProfileDTO() {
		super();
	}

	public ReviewProfileDTO(Long id, MediaContentReviewDTO mediaContent, String content, String createdAt, Integer rating) {
		super();
		this.id = id;
		this.mediaContent = mediaContent;
		this.content = content;
		this.createdAt = createdAt;
		this.rating = rating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MediaContentReviewDTO getMediaContent() {
		return mediaContent;
	}

	public void setMediaContent(MediaContentReviewDTO mediaContent) {
		this.mediaContent = mediaContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
}
