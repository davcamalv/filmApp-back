package com.davcamalv.filmApp.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Price;
import com.davcamalv.filmApp.dtos.PlatformWithPriceDTO;
import com.davcamalv.filmApp.enums.PriceType;
import com.davcamalv.filmApp.repositories.PriceRepository;

@Service
@Transactional
public class PriceService {

	protected final Logger log = Logger.getLogger(PriceService.class);

	@Autowired
	private PriceRepository priceRepository;

	public List<PlatformWithPriceDTO> getPlatformsByMediaContentAndPriceType(MediaContent mediaContent,
			PriceType priceType) {
		List<PlatformWithPriceDTO> res;
		List<Price> prices = priceRepository.findByMediaContentAndPriceType(mediaContent, priceType);
		res = prices.stream().map(x -> new PlatformWithPriceDTO(x.getPlatform().getName(), x.getPlatform().getLogo(),
				String.valueOf(x.getCost()) + "€")).collect(Collectors.toList());
		return res;
	}

	public Price save(Price price) {
		return priceRepository.save(price);
	}
}
