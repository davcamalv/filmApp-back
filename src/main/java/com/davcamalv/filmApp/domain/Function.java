package com.davcamalv.filmApp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "chat_function")
public class Function {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotNull
	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "button_value")
	private String button_value;
	
	@NotNull
	@Column(name = "button_label")
	private String button_label;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getButton_value() {
		return button_value;
	}

	public void setButton_value(String button_value) {
		this.button_value = button_value;
	}

	public String getButton_label() {
		return button_label;
	}

	public void setButton_label(String button_label) {
		this.button_label = button_label;
	}

	public Function(@NotNull String description, @NotNull String button_value, @NotNull String button_label) {
		super();
		this.description = description;
		this.button_value = button_value;
		this.button_label = button_label;
	}

	public Function() {
		super();
	}
	
}
