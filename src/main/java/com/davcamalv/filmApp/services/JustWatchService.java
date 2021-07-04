package com.davcamalv.filmApp.services;

import javax.transaction.Transactional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class JustWatchService {

	public void getPremieres() {
		try {
			ChromeOptions options = new ChromeOptions();
			options.setHeadless(false);
			WebDriver webDriver = new ChromeDriver(options);
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			webDriver.get("https://www.justwatch.com/es/nuevo");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("timeline")));
		} catch (Exception e) {
			System.out.println("Selenium error");
		} finally {
			
		}
	}
}
