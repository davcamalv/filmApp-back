package com.davcamalv.filmApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

}
