package com.davcamalv.filmApp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "option_value")
public class Option {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name = "label")
	private String label;
	
	@NotNull
	@Column(name = "option_text")
	private String text;
	
	@Column(name = "image")
	private String image;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Option(String label, @NotNull String text, String image) {
		super();
		this.label = label;
		this.text = text;
		this.image = image;
	}

	public Option() {
		super();
	}
	
	
}
