package com.davcamalv.filmApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Price;
import com.davcamalv.filmApp.enums.PriceType;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long>{

	List<Price> findByMediaContentAndPriceType(MediaContent mediaContent, PriceType priceType);


}