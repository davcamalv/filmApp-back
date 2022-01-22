package com.davcamalv.filmApp.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.repositories.PremiereRepository;

@Service
@Transactional
public class PremiereService {

	protected final Logger log = Logger.getLogger(PremiereService.class);
	
	@Autowired
	private PremiereRepository premiereRepository;
	
	public Premiere save(Premiere premiere) {
		return premiereRepository.save(premiere);
	}
	
	public List<Premiere> getPremiereByDateAndPlatform(Date date, Platform platform){
		Long id = null;
		if(platform != null) {
			id = platform.getId();
		}
		return premiereRepository.findByPremiereDate(date, id);
	}
	
	public List<Premiere> findAll(){
		return premiereRepository.findAll();
	}

	public Optional<Premiere> findOne(long i) {
		return premiereRepository.findById(i);
	}
}
