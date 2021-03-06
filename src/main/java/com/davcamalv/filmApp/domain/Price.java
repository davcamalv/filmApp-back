package com.davcamalv.filmApp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.davcamalv.filmApp.enums.PriceType;

@Entity
@Table(name = "price")
public class Price {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column(name = "cost")
	private String cost;
	
    @NotNull
    @Column(name = "price_type")
	@Enumerated(EnumType.STRING)
	private PriceType priceType;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "media_content_id", nullable = false)
	private MediaContent mediaContent;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "platform_id", nullable = false)
	private Platform platform;

	@NotNull
	@Column(name = "url", length = 3000)
	private String url;
	
	public Price(@NotNull String cost, @NotNull PriceType priceType, MediaContent mediaContent, Platform platform, String url) {
		super();
		this.cost = cost;
		this.priceType = priceType;
		this.mediaContent = mediaContent;
		this.platform = platform;
		this.url = url;
	}

	public Price() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
