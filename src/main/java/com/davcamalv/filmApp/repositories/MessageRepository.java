package com.davcamalv.filmApp.repositories;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Message;
import com.davcamalv.filmApp.domain.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long>{
	
	List<Message> findByUser(Pageable pageable, User user);
}
