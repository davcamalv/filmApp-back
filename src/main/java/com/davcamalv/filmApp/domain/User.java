package com.davcamalv.filmApp.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "chat_user")
public class User {
	
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@SafeHtml
	@Column(name = "name")
	private String name;
	
	@NotNull
	@SafeHtml
	@Column(name = "username", unique = true, nullable = false)
	private String username;
	
	@NotNull
	@SafeHtml
	@Column(name = "password")
	private String password;
	
	@ManyToMany
	@JoinTable(name = "user_rol", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
	private Set<Rol> roles = new HashSet<>();
	
	@Email
	@SafeHtml
	@Column(name = "email")
	private String email;

	public User(String name, @NotNull String username, @NotNull String password, @Email String email) {
		super();
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User() {
		super();
	}
	
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}
}
