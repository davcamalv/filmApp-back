package com.davcamalv.filmApp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Premiere;

@Repository
public interface PremiereRepository extends JpaRepository<Premiere, Long> {

	@Query("select p from Premiere p where (:date is null or p.premiereDate = :date) and (:platformId is null or p.platform.id = :platformId)")
	List<Premiere> findByPremiereDate(@Param("date") Date date, @Param("platformId") Long platformId);

}