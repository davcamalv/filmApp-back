package com.davcamalv.filmApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.MediaContent;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, Long>{

	Optional<MediaContent> findByJustWatchUrl(String justWatchUrl);
}