package com.davcamalv.filmApp.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "selectable")
public class Selectable {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name = "title")
	@NotNull
	private String title;
	
	@NotNull
	@Column(name = "description")
	private String description;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "option_selectable", joinColumns = @JoinColumn(name = "selectable_id"), inverseJoinColumns = @JoinColumn(name = "option_id"))
	private List<Option> options;

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

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public Selectable(@NotNull String title, @NotNull String description, List<Option> options) {
		super();
		this.title = title;
		this.description = description;
		this.options = options;
	}

	public Selectable() {
		super();
	}
	
}
