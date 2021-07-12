package com.davcamalv.filmApp.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.davcamalv.filmApp.enums.MediaType;

@Entity
@Table(name = "media_content")
public class MediaContent {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    @Column(name = "title")
	private String title;
	
    @Column(name = "description")
	private String description;
	
    @NotNull
    @Column(name = "media_type")
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;
	
	@Column(name = "creation_date")
	private String creationDate;
	
	@NotNull
	@Column(name = "just_watch_url")
	private String justWatchUrl;
	
	@Column(name = "imdb_id", unique = true)
	private String imdbId;
	
	@Column(name = "poster")
	private String poster;
	
	@Column(name = "score")
	private String score;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "media_content_platform", joinColumns = @JoinColumn(name = "media_content_id"), inverseJoinColumns = @JoinColumn(name = "platform_id"))
	private List<Platform> platforms;
	
	@Column(name = "search_performed")
	private boolean searchPerformed;

	public MediaContent(String title, String description, MediaType mediaType, String creationDate, String justWatchUrl,
			String imdbId, String poster, String score, List<Platform> platforms) {
		super();
		this.title = title;
		this.description = description;
		this.mediaType = mediaType;
		this.creationDate = creationDate;
		this.justWatchUrl = justWatchUrl;
		this.imdbId = imdbId;
		this.poster = poster;
		this.score = score;
		this.platforms = platforms;
	}

	public MediaContent() {
		super();
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getJustWatchUrl() {
		return justWatchUrl;
	}

	public void setJustWatchUrl(String justWatchUrl) {
		this.justWatchUrl = justWatchUrl;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
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

	public List<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<Platform> platforms) {
		this.platforms = platforms;
	}

	public boolean getSearchPerformed() {
		return searchPerformed;
	}

	public void setSearchPerformed(boolean searchPerformed) {
		this.searchPerformed = searchPerformed;
	}
	
}
