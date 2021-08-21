package com.davcamalv.filmApp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "genre")
public class Genre {
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@Column(name = "short_name")
	private String shortName;
	
	@Column(name = "tmdb_id")
	private Integer tmdbId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getTmdbId() {
		return tmdbId;
	}

	public void setTmdbId(Integer tmdbId) {
		this.tmdbId = tmdbId;
	}

	public Genre(@NotNull String name, String shortName, Integer tmdbId) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.tmdbId = tmdbId;
	}

	public Genre() {
		super();
	}
	
}
