package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.controllers.PruebasController;

@Service
@Transactional
public class JustWatchService {

	protected final Logger log = Logger.getLogger(JustWatchService.class);

	public void getPremieres() {
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		WebDriver webDriver = new ChromeDriver(options);
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			webDriver.get("https://www.justwatch.com/es/nuevo");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("timeline")));
		} catch (Exception e) {
			log.error("selenium error", e);
		} finally {
			webDriver.close();
		}
	}
}
