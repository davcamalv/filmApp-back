package com.davcamalv.filmApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Rol;
import com.davcamalv.filmApp.enums.RoleName;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
	
	Optional<Rol> findByRoleName(RoleName roleName);	
	
}