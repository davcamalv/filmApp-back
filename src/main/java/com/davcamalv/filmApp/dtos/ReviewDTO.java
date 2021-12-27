package com.davcamalv.filmApp.dtos;

import java.util.Date;

public class ReviewDTO {
	
	private AuthorDetailsDTO author_details;
	
	private String content;
	
	private Date created_at;
	
	private Integer rating;

	public AuthorDetailsDTO getAuthor_details() {
		return author_details;
	}

	public void setAuthor_details(AuthorDetailsDTO author_details) {
		this.author_details = author_details;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public ReviewDTO(AuthorDetailsDTO author_details, String content, Date created_at, Integer rating) {
		super();
		this.author_details = author_details;
		this.content = content;
		this.created_at = created_at;
		this.rating = rating;
	}

	public ReviewDTO() {
		super();
	}
	
}
