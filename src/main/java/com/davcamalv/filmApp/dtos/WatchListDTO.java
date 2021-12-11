package com.davcamalv.filmApp.dtos;

public class WatchListDTO {
	
	private Long id;

	private String title;
    
	private String score;

	private String creationDate;
	
	private String mediaType;
	
	private String poster;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public WatchListDTO(Long id, String title, String score, String creationDate, String mediaType, String poster) {
		super();
		this.id = id;
		this.title = title;
		this.score = score;
		this.creationDate = creationDate;
		this.mediaType = mediaType;
		this.poster = poster;
	}

	public WatchListDTO() {
		super();
	}
	
}
