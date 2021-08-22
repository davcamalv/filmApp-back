package com.davcamalv.filmApp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>{

	Optional<Genre> findByName(String name);

	Optional<Genre> findByTmdbId(Integer tmdbId);
}
