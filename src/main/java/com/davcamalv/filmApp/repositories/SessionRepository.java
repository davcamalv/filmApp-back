package com.davcamalv.filmApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Session;
import com.davcamalv.filmApp.domain.User;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>{
	
	Session findByUser(User user);
	
}
