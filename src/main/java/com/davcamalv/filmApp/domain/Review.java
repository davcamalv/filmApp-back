package com.davcamalv.filmApp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "review")
public class Review {

	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    @Temporal(TemporalType.DATE)
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "rating")
	private Integer rating;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "media_content_id", nullable = false)
	private MediaContent mediaContent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MediaContent getMediaContent() {
		return mediaContent;
	}

	public void setMediaContent(MediaContent mediaContent) {
		this.mediaContent = mediaContent;
	}

	public Review(@NotNull Long id, Date createdAt, String content, Integer rating, User user,
			MediaContent mediaContent) {
		super();
		this.id = id;
		this.createdAt = createdAt;
		this.content = content;
		this.rating = rating;
		this.user = user;
		this.mediaContent = mediaContent;
	}

	public Review() {
		super();
	}
	
}
