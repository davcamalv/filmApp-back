package com.davcamalv.filmApp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Genre;
import com.davcamalv.filmApp.dtos.ProfileGenresDTO;
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

	public List<ProfileGenresDTO> findAll() {
		List<Genre> genres = genreRepository.findAll();
		List<String> genresNames = new ArrayList<String>();
		List<ProfileGenresDTO> res = new ArrayList<ProfileGenresDTO>();
		for (Genre genre : genres) {
			if(!genresNames.contains(genre.getName())) {
				genresNames.add(genre.getName());
				res.add(new ProfileGenresDTO(genre.getId(), genre.getName()));
			}
		}
		return res;
	}
	
	public List<Genre> findByIds(List<Long> ids) {
		return genreRepository.findAllById(ids);
	}

}
