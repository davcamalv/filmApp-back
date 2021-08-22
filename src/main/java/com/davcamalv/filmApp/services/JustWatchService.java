package com.davcamalv.filmApp.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Genre;
import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.domain.Price;
import com.davcamalv.filmApp.dtos.MediaContentDTO;
import com.davcamalv.filmApp.dtos.MediaContentTMDBDTO;
import com.davcamalv.filmApp.dtos.PlatformWithPriceDTO;
import com.davcamalv.filmApp.dtos.SearchDTO;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.enums.PriceType;
import com.davcamalv.filmApp.utils.Constants;
import com.davcamalv.filmApp.utils.Utils;

@Service
@Transactional
public class JustWatchService {

	protected final Logger log = Logger.getLogger(JustWatchService.class);

	@Autowired
	private PlatformService platformService;

	@Autowired
	private GenreService genreService;

	@Autowired
	private PremiereService premiereService;

	@Autowired
	private MediaContentService mediaContentService;

	@Autowired
	private PriceService priceService;
	
	@Autowired
	private TMDBService tmdbService;

	@Scheduled(cron = "0 0 3 * * ?", zone = "Europe/Paris")
	protected void scrapePremieres() {
		WebDriver webDriver = Utils.createWebDriver();
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
					platformLogo = providerBlock.findElement(By.tagName("img")).getAttribute("src").replace("s25",
							"s100");
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
				if (poster.contains(Constants.DATA_IMAGE)) {
					poster = imgs.get(0).getAttribute(Constants.DATA_SRC).replace("s166", "s718");
				}
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

	public List<SearchDTO> getFilteredSearches(Map<String, String> parameters) {
		String url = getURLFilteredSearches(parameters);
		List<SearchDTO> res = new ArrayList<>();
		WebDriver webDriver = Utils.createWebDriver();
		String justWatchUrl;
		String searchTitle;
		String poster;

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			webDriver.get(url);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-list")));
			List<WebElement> searches = webDriver.findElements(By.className("title-list-grid__item"));
			for (WebElement search : searches) {
				justWatchUrl = search.findElement(By.tagName("a")).getAttribute("href");
				searchTitle = search.findElement(By.tagName("a")).findElement(By.tagName("div"))
						.findElement(By.tagName("picture")).findElement(By.tagName("img")).getAttribute("alt");
				List<WebElement> imgs = search.findElements(By.tagName("img"));
				if (!imgs.isEmpty()) {
					poster = imgs.get(0).getAttribute("src").replace("s166", "s718");
					if (poster.contains(Constants.DATA_IMAGE)) {
						poster = imgs.get(0).getAttribute(Constants.DATA_SRC).replace("s166", "s718");
					}
				} else {
					poster = null;
				}
				res.add(new SearchDTO(justWatchUrl, searchTitle, null, poster));
				mediaContentService.getOrCreateByJustWatchUrl(justWatchUrl, searchTitle, poster, null);
			}
		} catch (Exception e) {
			log.error("Error getting filtered searches", e);
		} finally {
			webDriver.close();
		}
		return res;
	}

	private String getURLFilteredSearches(Map<String, String> parameters) {
		String typeFilter = "";
		String type = "";
		if (parameters.get("tipo") != null && !"".equals(parameters.get("tipo"))) {
			type = "Película".equals(parameters.get("tipo")) ? "peliculas" : "series";
			typeFilter = "/" + type;
		}

		String platformFilter = "";
		if (parameters.get(Constants.PLATAFORMA) != null && !"".equals(parameters.get(Constants.PLATAFORMA))) {
			platformFilter = "providers="
					+ platformService.getByName(parameters.get(Constants.PLATAFORMA)).get().getShortName() + "&";
		}

		String ageFilter = "";
		if (parameters.get(Constants.CLASIFICACION_EDAD) != null
				&& !"".equals(parameters.get(Constants.CLASIFICACION_EDAD)) && !"series".equals(type)) {
			ageFilter = "age_certifications=" + parameters.get(Constants.CLASIFICACION_EDAD).split("\\.")[0] + "&";
		}

		String genreFilter = "";
		if (parameters.get(Constants.GENERO) != null && !"".equals(parameters.get(Constants.GENERO))) {
			genreFilter = "genres=" + genreService.getByName(parameters.get(Constants.GENERO)).get().getShortName()
					+ "&";
		}
		String yearFrom = "1900";
		if (parameters.get(Constants.FECHA_INICIAL) != null && !"".equals(parameters.get(Constants.FECHA_INICIAL))) {
			yearFrom = parameters.get(Constants.FECHA_INICIAL).split("\\.")[0];
		}
		String yearUntil = "3000";
		if (parameters.get(Constants.FECHA_FINAL) != null && !"".equals(parameters.get(Constants.FECHA_FINAL))) {
			yearUntil = parameters.get(Constants.FECHA_FINAL).split("\\.")[0];
		}

		String yearFilter = "release_year_from=" + yearFrom + "&release_year_until=" + yearUntil;

		return "https://www.justwatch.com/es" + typeFilter + "?" + platformFilter + ageFilter + genreFilter + yearFilter
				+ "&sort_by=imdb_score";
	}

	public List<SearchDTO> getSearches(String title) {
		List<SearchDTO> res = new ArrayList<>();
		WebDriver webDriver = Utils.createWebDriver();
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		String justWatchUrl;
		String year;
		String searchTitle;
		String poster;

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			String url = "https://www.justwatch.com/es/buscar?q="
					+ URLEncoder.encode(title, StandardCharsets.UTF_8.name());
			webDriver.get(url);
			js.executeScript("window.scrollTo(0,document.body.scrollHeight)");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title-list-row")));
			List<WebElement> searches = webDriver.findElements(By.tagName("ion-row"));
			for (WebElement search : searches) {
				justWatchUrl = search.findElement(By.tagName("a")).getAttribute("href");
				searchTitle = search.findElement(By.className("title-list-row__row__title")).getText();
				year = search.findElement(By.className("title-list-row__row--muted")).getText();
				List<WebElement> imgs = search.findElements(By.tagName("img"));
				if (!imgs.isEmpty()) {
					poster = imgs.get(0).getAttribute("src").replace("s166", "s718");
					if (poster.contains(Constants.DATA_IMAGE)) {
						poster = imgs.get(0).getAttribute(Constants.DATA_SRC).replace("s166", "s718");
					}
				} else {
					poster = null;
				}
				res.add(new SearchDTO(justWatchUrl, searchTitle, year, poster));
				mediaContentService.getOrCreateByJustWatchUrl(justWatchUrl, searchTitle, poster, year);
			}
		} catch (Exception e) {
			log.error("Error getting the searches", e);
		} finally {
			webDriver.close();
		}
		return res;
	}

	public MediaContentDTO getMediaContent(String url) {
		MediaContentDTO res = null;

		Optional<MediaContent> mediaContent = mediaContentService.getByJustWatchUrl(url);
		if (mediaContent.isPresent() && mediaContent.get().getSearchPerformed()) {
			MediaContent mediaContentValue = mediaContent.get();
			res = new MediaContentDTO(mediaContentValue.getTitle(), mediaContentValue.getDescription(),
					mediaContentValue.getMediaType().name(), mediaContentValue.getCreationDate(),
					mediaContentValue.getPoster(), mediaContentValue.getScore(),
					priceService.getPlatformsByMediaContentAndPriceType(mediaContentValue, PriceType.RENT),
					priceService.getPlatformsByMediaContentAndPriceType(mediaContentValue, PriceType.STREAM),
					priceService.getPlatformsByMediaContentAndPriceType(mediaContentValue, PriceType.BUY));
		} else if (mediaContent.isPresent()) {
			MediaContent mediaContentValue = mediaContent.get();
			res = scrapeMediaContent(url, mediaContentValue);
		}
		return res;
	}

	public MediaContentDTO scrapeMediaContent(String url, MediaContent mediaContentValue) {
		MediaContentDTO res = null;
		String score = null;
		String imdbId = null;

		WebDriver webDriver = Utils.createWebDriver();
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 15);
			webDriver.get(url);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("jw-info-box")));
			String creationDate = webDriver.findElement(By.className("text-muted")).getText();
			String description = webDriver.findElement(By.xpath("//p[contains(@class, 'text-wrap-pre-line')]/span"))
					.getText();
			List<WebElement> imdb = webDriver.findElements(By.xpath("//*[@v-uib-tooltip='IMDB']"));
			if (!imdb.isEmpty()) {
				score = imdb.get(0).findElement(By.tagName("a")).getText();
				imdbId = imdb.get(0).findElement(By.tagName("a")).getAttribute("href");
				imdbId = imdbId.replace("https://www.imdb.com/title/", "").split("//?")[0];
				MediaContentTMDBDTO mediaContentTMDBDTO = tmdbService.getMediaContentByImdbID(imdbId);
				setTmdbIdAndGenres(mediaContentTMDBDTO, mediaContentValue);
			}
			mediaContentValue.setCreationDate(creationDate);
			mediaContentValue.setDescription(description);
			mediaContentValue.setScore(score);
			mediaContentValue.setImdbId(imdbId);
			mediaContentValue.setSearchPerformed(true);
			mediaContentService.save(mediaContentValue);
			List<WebElement> monetizations = webDriver.findElements(By.className("monetizations"));
			WebElement monetization = monetizations.get(monetizations.size() - 1);
			List<WebElement> rents = monetization.findElements(By.className("price-comparison__grid__row--rent"));
			List<WebElement> streams = monetization.findElements(By.className("price-comparison__grid__row--stream"));
			List<WebElement> buyList = monetization.findElements(By.className("price-comparison__grid__row--buy"));
			List<PlatformWithPriceDTO> rent = scrapePrices(rents, mediaContentValue, PriceType.RENT);
			List<PlatformWithPriceDTO> stream = scrapePrices(streams, mediaContentValue, PriceType.STREAM);
			List<PlatformWithPriceDTO> buy = scrapePrices(buyList, mediaContentValue, PriceType.BUY);
			res = new MediaContentDTO(mediaContentValue.getTitle(), mediaContentValue.getDescription(),
					mediaContentValue.getMediaType().name(), mediaContentValue.getCreationDate(),
					mediaContentValue.getPoster(), mediaContentValue.getScore(), rent, stream, buy);
		} catch (Exception e) {
			log.error("Error scraping media content", e);
		} finally {
			webDriver.close();
		}
		return res;
	}

	private void setTmdbIdAndGenres(MediaContentTMDBDTO mediaContentTMDBDTO, MediaContent mediaContentValue) {
		Set<Genre> genres = null;
		if(mediaContentValue.getMediaType().equals(MediaType.MOVIE)) {
			if(mediaContentTMDBDTO != null && mediaContentTMDBDTO.getMovie_results() != null && !mediaContentTMDBDTO.getMovie_results().isEmpty()) {
				mediaContentValue.setTmdbId(mediaContentTMDBDTO.getMovie_results().get(0).getId());
				genres = mediaContentTMDBDTO.getMovie_results().get(0).getGenre_ids().stream().map(x -> genreService.getByTmdbId(x).get()).collect(Collectors.toSet());
			}
			
		}else {
			if(mediaContentTMDBDTO != null && mediaContentTMDBDTO.getTv_results() != null && !mediaContentTMDBDTO.getTv_results().isEmpty()) {
				mediaContentValue.setTmdbId(mediaContentTMDBDTO.getTv_results().get(0).getId());
				genres = mediaContentTMDBDTO.getTv_results().get(0).getGenre_ids().stream().map(x -> genreService.getByTmdbId(x).get()).collect(Collectors.toSet());
			}
		}
		
		mediaContentValue.setGenres(genres);
	}
	
	private List<PlatformWithPriceDTO> scrapePrices(List<WebElement> webElements, MediaContent mediaContent,
			PriceType priceType) {
		List<PlatformWithPriceDTO> res = new ArrayList<>();
		String url;
		String logo;
		String name;
		String cost;
		Platform platform;
		Price price;
		List<WebElement> prices;
		if (!webElements.isEmpty()) {
			prices = webElements.get(0).findElements(By.className("price-comparison__grid__row__element__icon"));
			for (WebElement priceElement : prices) {
				url = priceElement.findElement(By.tagName("a")).getAttribute("href");
				logo = priceElement.findElement(By.tagName("img")).getAttribute("src");
				name = priceElement.findElement(By.tagName("img")).getAttribute("alt");
				cost = priceElement.findElement(By.className("price-comparison__grid__row__price")).getText();
				platform = platformService.getOrCreateByName(name, logo);
				price = new Price(cost, priceType, mediaContent, platform, url);
				priceService.save(price);
				res.add(new PlatformWithPriceDTO(name, logo, cost, url));
			}
		}
		return res;
	}

}
