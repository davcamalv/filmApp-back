package com.davcamalv.filmApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long>{

	Configuration findByProperty(String property);
}
