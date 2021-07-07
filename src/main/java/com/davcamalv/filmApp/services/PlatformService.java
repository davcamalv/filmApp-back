package com.davcamalv.filmApp.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.repositories.PlatformRepository;

@Service
@Transactional
public class PlatformService {

	protected final Logger log = Logger.getLogger(PlatformService.class);
	
	@Autowired
	private PlatformRepository platformRepository;
	
	public Platform getOrCreateByName(String name, String logo) {
		Platform res;
		Optional<Platform> platformBD = platformRepository.findByName(name);
		if(platformBD.isPresent()) {
			res = platformBD.get();
		}else {
			res = platformRepository.save(new Platform(name, logo));
		}
		return res;
	}

	public Optional<Platform> getByName(String name) {
		return platformRepository.findByName(name);
	}
}
