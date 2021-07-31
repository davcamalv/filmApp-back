package com.davcamalv.filmApp.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Rol;
import com.davcamalv.filmApp.enums.RoleName;
import com.davcamalv.filmApp.repositories.RolRepository;

@Service
@Transactional
public class RolService {

	@Autowired
	private RolRepository rolRepository;
	
	public Optional<Rol> getByRoleName(RoleName roleName){
		return rolRepository.findByRoleName(roleName);
	}
}
