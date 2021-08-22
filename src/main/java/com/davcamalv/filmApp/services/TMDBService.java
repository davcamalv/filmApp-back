package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davcamalv.filmApp.dtos.MediaContentTMDBDTO;
import com.davcamalv.filmApp.utils.Constants;

@Service
@Transactional
public class TMDBService {

	@Autowired
	private ConfigurationService configurationService;
	
	public MediaContentTMDBDTO getMediaContentByImdbID(String imdbID) {
		RestTemplate restTemplate = new RestTemplate();
		String url = Constants.TMBD_BASE_URL + "find/" + imdbID + "?api_key="
				+ configurationService.getByProperty("tmdb.apikey").getValue() + "&language=es-ES&external_source=imdb_id";
		return restTemplate.getForEntity(url, MediaContentTMDBDTO.class).getBody();
	}

}
