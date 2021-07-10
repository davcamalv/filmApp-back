package com.davcamalv.filmApp.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.repositories.MediaContentRepository;

@Service
@Transactional
public class MediaContentService {

	protected final Logger log = Logger.getLogger(MediaContentService.class);
	
	@Autowired
	private MediaContentRepository mediaContentRepository;
	
	public MediaContent save(MediaContent mediaContent) {
		return mediaContentRepository.save(mediaContent);
	}
	
	public MediaContent getOrCreateByJustWatchUrl(String justWatchUrl, String title, String poster, String creationDate) {
		MediaContent res;
		Optional<MediaContent> mediaContentBD = mediaContentRepository.findByJustWatchUrl(justWatchUrl);
		if(mediaContentBD.isPresent()) {
			res = mediaContentBD.get();
		}else {
			MediaType mediaType = getMediaTypeByUrl(justWatchUrl);
			res = mediaContentRepository.save(new MediaContent(title, null, mediaType, creationDate, justWatchUrl, null, poster, null, null));
		}
		return res;
	}
	
	private MediaType getMediaTypeByUrl(String url) {
		MediaType res = MediaType.MOVIE;
		if(url != null && url.contains("serie")) {
			res = MediaType.SERIE;
		}
		return res;
	}
}
