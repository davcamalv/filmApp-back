package com.davcamalv.filmApp.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Genre;
import com.davcamalv.filmApp.repositories.GenreRepository;

@Service
@Transactional
public class GenreService {
	
	@Autowired
	private GenreRepository genreRepository;
	
	public Optional<Genre> getByName(String name) {
		return genreRepository.findByName(name);
	}
	
	public Optional<Genre> getByTmdbId(Integer tmdbId) {
		return genreRepository.findByTmdbId(tmdbId);
	}
	
}
