package com.davcamalv.filmApp.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Function;
import com.davcamalv.filmApp.repositories.FunctionRepository;

@Service
@Transactional
public class FunctionService {

	@Autowired
	private FunctionRepository functionRepository;
	
	public List<Function> findAll() {
		return functionRepository.findAll();
	}
}
