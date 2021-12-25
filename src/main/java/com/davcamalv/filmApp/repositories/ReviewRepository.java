package com.davcamalv.filmApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

}
