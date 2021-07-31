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
@Table(name = "premiere")
public class Premiere {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
    @Temporal(TemporalType.DATE)
	@Column(name = "premiere_date")
	private Date premiereDate;
	
	@Column(name = "season")
	private String season;
	
	@Column(name = "news")
	private String news;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "media_content_id", nullable = false)
	private MediaContent mediaContent;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "platform_id", nullable = false)
	private Platform platform;

	public Premiere(@NotNull Date premiereDate, String season, String news, MediaContent mediaContent,
			Platform platform) {
		super();
		this.premiereDate = premiereDate;
		this.season = season;
		this.news = news;
		this.mediaContent = mediaContent;
		this.platform = platform;
	}

	public Premiere() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getPremiereDate() {
		return premiereDate;
	}

	public void setPremiereDate(Date premiereDate) {
		this.premiereDate = premiereDate;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	public MediaContent getMediaContent() {
		return mediaContent;
	}

	public void setMediaContent(MediaContent mediaContent) {
		this.mediaContent = mediaContent;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
}
