package com.davcamalv.filmApp.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.dtos.CreditsDTO;
import com.davcamalv.filmApp.dtos.MediaContentTMDBDTO;
import com.davcamalv.filmApp.dtos.PersonDTO;
import com.davcamalv.filmApp.dtos.ReviewListDTO;
import com.davcamalv.filmApp.dtos.SearchPersonDTO;
import com.davcamalv.filmApp.dtos.TrailerDTO;
import com.davcamalv.filmApp.dtos.TrailerResultDTO;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.utils.Constants;

@Service
@Transactional
public class TMDBService {

	@Autowired
	private ConfigurationService configurationService;

	public MediaContentTMDBDTO getMediaContentByImdbID(String imdbID) {
		RestTemplate restTemplate = new RestTemplate();
		String url = Constants.TMBD_BASE_URL + "find/" + imdbID + "?api_key="
				+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue()
				+ "&language=es-ES&external_source=imdb_id";
		return restTemplate.getForEntity(url, MediaContentTMDBDTO.class).getBody();
	}

	public String getTrailer(MediaContent mediaContent) {
		String res = "";
		TrailerDTO response = null;
		String url = Constants.TMBD_BASE_URL;
		RestTemplate restTemplate = new RestTemplate();
		MediaContentTMDBDTO mediaContentTMDBDTO = getMediaContentByImdbID(mediaContent.getImdbId());
		if (mediaContentTMDBDTO != null && mediaContentTMDBDTO.getMovie_results() != null
				&& !mediaContentTMDBDTO.getMovie_results().isEmpty()) {
			String type = mediaContent.getMediaType().equals(MediaType.MOVIE) ? "movie/" : "tv/";
			url = url + type + mediaContentTMDBDTO.getMovie_results().get(0).getId() + "/videos?api_key="
					+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue() + "&language=es-ES";

			response = restTemplate.getForEntity(url, TrailerDTO.class).getBody();
		}

		if (response != null) {
			res = getTrailerUrl(response.getResults());
		}
		return res;
	}

	private String getTrailerUrl(List<TrailerResultDTO> videos) {
		return videos.stream().filter(x -> "YouTube".equals(x.getSite()) && "Trailer".equals(x.getType()))
				.map(x -> x.getKey()).findFirst().orElse("");
	}

	public CreditsDTO getCastByMediaContent(MediaContent mediaContent) {
		CreditsDTO res = new CreditsDTO(new ArrayList<>(), new ArrayList<>());
		String url = Constants.TMBD_BASE_URL;
		RestTemplate restTemplate = new RestTemplate();
		MediaContentTMDBDTO mediaContentTMDBDTO = getMediaContentByImdbID(mediaContent.getImdbId());
		if (mediaContentTMDBDTO != null && mediaContentTMDBDTO.getMovie_results() != null
				&& !mediaContentTMDBDTO.getMovie_results().isEmpty()) {
			String type = mediaContent.getMediaType().equals(MediaType.MOVIE) ? "movie/" : "tv/";
			url = url + type + mediaContentTMDBDTO.getMovie_results().get(0).getId() + "/credits?api_key="
					+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue() + "&language=es-ES";

			res = restTemplate.getForEntity(url, CreditsDTO.class).getBody();
		}
		return res;
	}

	public PersonDTO getDirector(List<PersonDTO> crew) {
		PersonDTO res = null;
		Integer id =  crew.stream().filter(x -> x.getJob() != null && "Director".equals(x.getJob())).findFirst()
				.map(x -> x.getId()).orElse(null);
		if(id != null) {
			res = getPersonByID(id);
		}
		return res;
	}
	
	public PersonDTO getPersonByID(Integer tmdbID) {
		RestTemplate restTemplate = new RestTemplate();
		String url = Constants.TMBD_BASE_URL + "person/" + tmdbID + "?api_key="
				+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue()
				+ "&language=es-ES";
		return restTemplate.getForEntity(url, PersonDTO.class).getBody();
	}
	
	public List<PersonDTO> searchPeople(String query) throws UnsupportedEncodingException {
		List<PersonDTO> res = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		String url = Constants.TMBD_BASE_URL + "search/person?api_key="
				+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue()
				+ "&language=es-ES&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
		SearchPersonDTO response = restTemplate.getForEntity(url, SearchPersonDTO.class).getBody();
		if(response != null) {
			res = response.getResults();
		}
		return res;
	}
	
	public ReviewListDTO getReviewsByMediaContent(MediaContent mediaContent) {
		ReviewListDTO res = new ReviewListDTO();
		String url = Constants.TMBD_BASE_URL;
		RestTemplate restTemplate = new RestTemplate();
		MediaContentTMDBDTO mediaContentTMDBDTO = getMediaContentByImdbID(mediaContent.getImdbId());
		if (mediaContentTMDBDTO != null && mediaContentTMDBDTO.getMovie_results() != null
				&& !mediaContentTMDBDTO.getMovie_results().isEmpty()) {
			String type = mediaContent.getMediaType().equals(MediaType.MOVIE) ? "movie/" : "tv/";
			url = url + type + mediaContentTMDBDTO.getMovie_results().get(0).getId() + "/reviews?api_key="
					+ configurationService.getByProperty(Constants.TMDB_APIKEY).getValue();

			res = restTemplate.getForEntity(url, ReviewListDTO.class).getBody();
		}
		return res;
	}

}
