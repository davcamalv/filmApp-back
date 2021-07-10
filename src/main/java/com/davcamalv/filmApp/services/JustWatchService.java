package com.davcamalv.filmApp.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.dtos.SearchDTO;

@Service
@Transactional
public class JustWatchService {

	protected final Logger log = Logger.getLogger(JustWatchService.class);

	@Autowired
	private PlatformService platformService;

	@Autowired
	private PremiereService premiereService;

	@Autowired
	private MediaContentService mediaContentService;

	@Scheduled(cron = "0 50 11 * * ?", zone = "Europe/Paris")
	protected void scrapePremieres() {
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		options.addArguments("window-size=1920,1080");
		WebDriver webDriver = new ChromeDriver(options);
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			webDriver.get("https://www.justwatch.com/es/nuevo");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("timeline")));
			List<WebElement> daysBlocks = new ArrayList<>();
			while (daysBlocks.size() < 2) {
				js.executeScript("window.scrollBy(0,1000)");
				daysBlocks = webDriver.findElements(By.className("timeline__header"));
			}
			Calendar actualDate = Calendar.getInstance();
			actualDate.setTime(new Date());
			String actualClassName = "timeline__timeframe--" + actualDate.get(Calendar.YEAR) + "-"
					+ String.format("%02d", actualDate.get(Calendar.MONTH) + 1) + "-"
					+ String.format("%02d", actualDate.get(Calendar.DAY_OF_MONTH));
			List<WebElement> todayPremieres = webDriver.findElements(By.className(actualClassName));
			if (!todayPremieres.isEmpty()) {
				List<WebElement> providersBlocks = todayPremieres.get(0)
						.findElements(By.className("timeline__provider-block"));
				Platform platform;

				String platformName = "";
				String platformLogo = "";

				List<WebElement> mediaContents;
				for (WebElement providerBlock : providersBlocks) {
					platformName = providerBlock.findElement(By.tagName("img")).getAttribute("alt");
					platformLogo = providerBlock.findElement(By.tagName("img")).getAttribute("src");
					platform = platformService.getOrCreateByName(platformName, platformLogo);
					mediaContents = providerBlock.findElements(By.className("horizontal-title-list__item"));
					getMediaContentData(mediaContents, platform);
				}
			}
		} catch (Exception e) {
			log.error("Error getting the premieres", e);
		} finally {
			webDriver.close();
		}
	}

	private void getMediaContentData(List<WebElement> mediaContents, Platform platform) {
		String justWatchUrl = "";
		String poster = "";
		String title = "";
		String news = "";
		String season = "";
		MediaContent mediaContent;
		for (WebElement mediaContentElement : mediaContents) {
			justWatchUrl = mediaContentElement.getAttribute("href");
			List<WebElement> imgs = mediaContentElement.findElements(By.tagName("img"));
			if (!imgs.isEmpty()) {
				title = imgs.get(0).getAttribute("alt");
				poster = imgs.get(0).getAttribute("src").replace("s166", "s718");
			} else {
				title = mediaContentElement.findElement(By.className("title-poster--no-poster")).getText();
				poster = null;
			}
			List<WebElement> newsElements = mediaContentElement.findElements(By.className("title-poster__badge__new"));
			if (!newsElements.isEmpty()) {
				news = newsElements.get(0).findElement(By.tagName("span")).getText();
			}
			List<WebElement> seasonElements = mediaContentElement.findElements(By.className("title-poster__badge"));
			if (!seasonElements.isEmpty()) {
				season = seasonElements.get(0).getText();
			}
			mediaContent = mediaContentService.getOrCreateByJustWatchUrl(justWatchUrl, title, poster, null);
			premiereService.save(new Premiere(new Date(), season, news, mediaContent, platform));
		}
	}

	public List<SearchDTO> getSearches(String title) {
		List<SearchDTO> res = new ArrayList<>();
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		options.addArguments("window-size=1920,1080");
		WebDriver webDriver = new ChromeDriver(options);
		String justWatchUrl;
		String year;
		String searchTitle;
		String poster;
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			String url = "https://www.justwatch.com/es/buscar?q=" + URLEncoder.encode(title, StandardCharsets.UTF_8.name());
			webDriver.get(url);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-list-row")));
			List<WebElement> searches = webDriver.findElements(By.tagName("ion-row"));
			for (WebElement search : searches) {
				justWatchUrl = search.findElement(By.tagName("a")).getAttribute("href");
				searchTitle = search.findElement(By.className("title-list-row__row__title")).getText();
				year = search.findElement(By.className("title-list-row__row--muted")).getText();
				res.add(new SearchDTO(justWatchUrl, searchTitle, year));
				List<WebElement> imgs = search.findElements(By.tagName("img"));
				if (!imgs.isEmpty()) {
					poster = imgs.get(0).getAttribute("src").replace("s166", "s718");
				} else {
					poster = null;
				}
				mediaContentService.getOrCreateByJustWatchUrl(justWatchUrl, searchTitle, poster, year);
			}
		} catch (Exception e) {
			log.error("Error getting the searches", e);
		} finally {
			webDriver.close();
		}
		return res;
	}
}
