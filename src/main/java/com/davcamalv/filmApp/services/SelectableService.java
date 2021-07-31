package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Selectable;
import com.davcamalv.filmApp.repositories.SelectableRepository;

@Service
@Transactional
public class SelectableService {

	@Autowired
	private SelectableRepository selectableRepository;
	
	public Selectable save(Selectable selectable) {
		return selectableRepository.save(selectable);
	}
	
	public Selectable findById(Long id) {
		return selectableRepository.findById(id).get();
	}
}
