package com.davcamalv.filmApp;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.services.PlatformService;
import com.davcamalv.filmApp.services.PremiereService;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
@Sql({"/test.sql"})
public class PremiereServiceTest {

	@Autowired
	PremiereService premiereService;
	
	@Autowired
	PlatformService platformService;
	
	@Test
	public void getPremiereByDateAndPlatformTest() {
		Date date = new GregorianCalendar(2021, Calendar.NOVEMBER, 12).getTime();
		Optional<Platform> platform = platformService.findOne(1l);
		List<Premiere> premieres = null;
		
		Optional<Premiere> premiere = premiereService.findOne(1l);
		if(platform.isPresent()) {
			premieres = premiereService.getPremiereByDateAndPlatform(date, platform.get());
		}
		assertFalse(premieres.isEmpty());
		if(premiere.isPresent()) {
			assertEquals(premiere.get().getId(), premieres.get(0).getId());
		}
	}
	
}
