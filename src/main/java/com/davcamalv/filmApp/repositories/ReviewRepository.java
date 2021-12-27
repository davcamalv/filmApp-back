package com.davcamalv.filmApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

	List<Review> findByMediaContent(MediaContent mediaContent);

}
