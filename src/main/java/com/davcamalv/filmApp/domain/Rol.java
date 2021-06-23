package com.davcamalv.filmApp.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.davcamalv.filmApp.enums.RoleName;

@Entity
public class Rol {

	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private RoleName roleName;

	public Rol() {
		super();
	}

	public Rol(@NotNull RoleName roleName) {
		super();
		this.roleName = roleName;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RoleName getRoleName() {
		return roleName;
	}

	public void setRoleName(RoleName roleName) {
		this.roleName = roleName;
	}
}
