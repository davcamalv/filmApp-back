package com.davcamalv.filmApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Platform;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long>{

	Optional<Platform> findByName(String name);

}