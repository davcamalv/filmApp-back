package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Configuration;
import com.davcamalv.filmApp.repositories.ConfigurationRepository;

@Service
@Transactional
public class ConfigurationService {

	@Autowired
	private ConfigurationRepository configurationRepository;
	
	public Configuration getByProperty(String property) {
		return configurationRepository.findByProperty(property);
	}
}
