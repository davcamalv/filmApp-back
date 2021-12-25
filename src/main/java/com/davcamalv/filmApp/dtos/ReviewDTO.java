package com.davcamalv.filmApp.dtos;

import java.util.Date;

public class ReviewDTO {
	
	private String author;
	
	private String content;
	
	private Date created_at;
	
	private Integer rating;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public ReviewDTO(String author, String content, Date created_at, Integer rating) {
		super();
		this.author = author;
		this.content = content;
		this.created_at = created_at;
		this.rating = rating;
	}

	public ReviewDTO() {
		super();
	}
	
}
