package com.davcamalv.filmApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Configuration;
import com.davcamalv.filmApp.repositories.ConfigurationRepository;

@Service
public class ConfigurationService {

	@Autowired
	private ConfigurationRepository configurationRepository;
	
	public Configuration getByProperty(String property) {
		return configurationRepository.findByProperty(property);
	}
}
