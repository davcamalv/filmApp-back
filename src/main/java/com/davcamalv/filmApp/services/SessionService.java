package com.davcamalv.filmApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Session;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.repositories.SessionRepository;

@Service
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;
	
	public Session getSessionByUser(User user) {
		return sessionRepository.findByUser(user);
	}

	public Session save(Session session) {
		return sessionRepository.save(session);
	}
}
