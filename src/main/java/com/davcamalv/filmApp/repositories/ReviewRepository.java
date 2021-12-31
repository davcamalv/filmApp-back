package com.davcamalv.filmApp.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.domain.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

	List<Review> findByMediaContentAndDraft(MediaContent mediaContent, boolean draft);

	List<Review> findByUserAndDraft(User user, boolean draft, Pageable pageable);

	boolean existsByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, boolean b);

	void deleteByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, Boolean draft);

	List<Review> findByMediaContentAndUserAndDraft(MediaContent mediaContent, User user, Boolean draft);

}
