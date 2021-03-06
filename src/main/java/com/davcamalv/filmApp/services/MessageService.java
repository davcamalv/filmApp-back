package com.davcamalv.filmApp.services;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Function;
import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.Message;
import com.davcamalv.filmApp.domain.Option;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.domain.Review;
import com.davcamalv.filmApp.domain.Selectable;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.MediaContentDTO;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.davcamalv.filmApp.dtos.OptionDTO;
import com.davcamalv.filmApp.dtos.PersonDTO;
import com.davcamalv.filmApp.dtos.PlatformWithPriceDTO;
import com.davcamalv.filmApp.dtos.ReviewDTO;
import com.davcamalv.filmApp.dtos.SearchDTO;
import com.davcamalv.filmApp.dtos.SelectableDTO;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.enums.SenderType;
import com.davcamalv.filmApp.repositories.MessageRepository;
import com.davcamalv.filmApp.utils.Constants;
import com.davcamalv.filmApp.utils.Utils;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

@Service
public class MessageService {
	
	protected final Logger log = Logger.getLogger(MessageService.class);

	private static final String USER_INPUT = "userInput";

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SelectableService selectableService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private UserService userService;

	@Autowired
	private PlatformService platformService;

	@Autowired
	private PremiereService premiereService;

	@Autowired
	private JustWatchService justWatchService;

	@Autowired
	private MediaContentService mediaContentService;

	@Autowired
	private TMDBService TMDBService;
	
	@Autowired
	private WatsonService watsonService;

	@Autowired
	private ReviewService reviewService;
	
	public List<MessageDTO> findMessagesByUser(int pageNumber, int pageSize, User user) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
		List<MessageDTO> res = messageRepository.findByUser(pageable, user).stream()
				.map(x -> new MessageDTO(x.getMessage(), x.getSender().name(), false, null, false))
				.collect(Collectors.toList());
		Collections.reverse(res);
		return res;
	}

	public Message saveUserMessage(MessageDTO messageDTO) {
		Message message = new Message(Utils.makeSafeMessage(messageDTO.getMessage()),
				SenderType.valueOf(messageDTO.getSender()), userService.getByUserLogged(), false, null, false);
		return messageRepository.save(message);
	}

	public Message save(MessageDTO messageDTO, Selectable selectable) {
		Message message = new Message(messageDTO.getMessage(), SenderType.valueOf(messageDTO.getSender()),
				userService.getByUserLogged(), messageDTO.getSpecialKeyboard(), selectable, messageDTO.getFullWidth());
		return messageRepository.save(message);
	}

	public MessageDTO processResponse(MessageResponse response, MessageDTO userMessage) {
		MessageDTO res;
		if (response.getOutput().getActions() != null && !response.getOutput().getActions().isEmpty()) {
			res = processResponseByReflection(response, userMessage);
		} else {
			res = processBasicResponse(response);
		}
		if (res.getSpecialKeyboard()) {
			save(res, selectableService.findById(res.getSelectable().getId()));
		} else {
			save(res, null);
		}
		return res;
	}

	private MessageDTO processBasicResponse(MessageResponse response) {
		MessageDTO res;
		Map<String, String> htmlAttributes = new HashMap<>();
		RuntimeResponseGeneric output = response.getOutput().getGeneric().get(0);
		switch (output.responseType()) {
		case "image":
			String source = Utils.makeSafeMessage(output.source());
			res = new MessageDTO(createImageMessage(source), SenderType.server.name(), false, null, false);
			break;
		case "option":
			String title = output.title();
			String description = output.description();
			List<DialogNodeOutputOptionsElement> options = output.options();
			res = createWatsonOptionMessage(title, description, options);
			break;
		default:
			String text = Utils.makeSafeMessage(output.text());
			htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
			String html = Utils.createHtmlTag(Constants.P, text, htmlAttributes);
			res = new MessageDTO(html, SenderType.server.name(), false, null, false);
			break;
		}
		return res;
	}

	private MessageDTO processResponseByReflection(MessageResponse response, MessageDTO userMessage) {
		MessageDTO res;
		try {
			String methodName = response.getOutput().getActions().get(0).getName();
			Method method = getClass().getDeclaredMethod(methodName, MessageResponse.class, String.class);
			res = (MessageDTO) method.invoke(this, response, userMessage.getMessage());
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			Map<String, String> htmlAttributes = new HashMap<>();
			htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
			String html = Utils.createHtmlTag(Constants.P,
					"Disculpe, actualmente no tengo implementada esa funcionalidad", htmlAttributes);
			res = new MessageDTO(html, SenderType.server.name(), false, null, false);
		}
		return res;
	}

	private Map<String, String> getParameters(MessageResponse response, String userInput) {
		Map<String, String> parameters = response.getOutput().getActions().get(0).getParameters().entrySet().stream()
				.collect(HashMap::new,
						(m, v) -> m.put(v.getKey(), v.getValue() == null ? null : String.valueOf(v.getValue())),
						HashMap::putAll);
		parameters.put(USER_INPUT, userInput);
		return parameters;
	}

	protected MessageDTO getPremieres(MessageResponse response, String userInput) throws ParseException {
		Map<String, String> parameters = getParameters(response, userInput);
		Date date = new Date();
		String dateStr = parameters.get("date");
		String platformStr = parameters.get("provider");
		if (dateStr != null && !"saltar".equals(dateStr)) {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		}
		Platform platform = null;
		if (platformStr != null && !"saltar".equals(platformStr)) {
			Optional<Platform> optionalPlatform = platformService.getByName(platformStr);
			if (optionalPlatform.isPresent()) {
				platform = optionalPlatform.get();
			} else {
				return getMessageError("No hay estrenos para la fecha y la plataforma especificadas");
			}
		}
		List<Premiere> premieres = premiereService.getPremiereByDateAndPlatform(date, platform);
		if (premieres.isEmpty()) {
			return getMessageError("No hay estrenos para la fecha y la plataforma especificadas");
		}
		return getPremiereMessage(premieres);
	}

	protected MessageDTO getSearches(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String title = parameters.get(USER_INPUT);
		List<Option> options;
		List<SearchDTO> searches = justWatchService.getSearches(title);
		if (searches.isEmpty()) {
			return getMessageError("Disculpa, no he encontrado ning??n resultado");
		} else {
			options = searches.stream().map(x -> new Option(x.getTitle() + " " + x.getYear(), x.getUrl(), x.getImage()))
					.collect(Collectors.toList());
		}
		return createOptionMessage("??A cu??l de los siguientes resultados se refiere?", "", options);
	}
	
	protected MessageDTO selectMyList(MessageResponse response, String userInput) {
		List<Option> options;
		List<MediaContent> myList = userService.getByUserLogged().getToWatchList();
		if (!myList.isEmpty()) {
			options = myList.stream().map(x -> new Option(x.getTitle() + (x.getCreationDate() != null?" " + x.getCreationDate(): ""), String.valueOf(x.getId()), x.getPoster()))
					.collect(Collectors.toList());
		}else {
			return getMessageError("A??n no tienes t??tulos en tu lista");
		}
		
		return createOptionMessage("??Cu??l de los t??tulos quieres eliminar de tu lista?", "", options);
	}
	
	protected MessageDTO deleteElementMediaContentList(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		Long id = Long.parseLong(parameters.get(USER_INPUT));
		MediaContent mediaContent = mediaContentService.findById(id);
		userService.deleteElementMediaContentList(id);
		return new MessageDTO("He eliminado '" + mediaContent.getTitle() + "' de tu lista", SenderType.server.name(), false, null, false);
	}

	protected MessageDTO getPeopleSearches(MessageResponse response, String userInput)
			throws UnsupportedEncodingException {
		Map<String, String> parameters = getParameters(response, userInput);
		String name = parameters.get(USER_INPUT);
		List<Option> options;
		List<PersonDTO> searches = TMDBService.searchPeople(name);
		if (searches.isEmpty()) {
			return getMessageError("Disculpa, no he encontrado ning??n resultado");
		} else {

			options = searches.stream()
					.map(x -> new Option(x.getName(), String.valueOf(x.getId()),
							x.getProfile_path() != null && !"".equals(x.getProfile_path())
									? Constants.TMBD_IMAGE_BASE_URL + x.getProfile_path()
									: null))
					.collect(Collectors.toList());
		}
		return createOptionMessage("??A cu??l de los siguientes resultados se refiere?", "", options);
	}

	protected MessageDTO getPerson(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = getPersonMessage(TMDBService.getPersonByID(Integer.parseInt(parameters.get(USER_INPUT))));
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	protected MessageDTO getFilteredSearches(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		List<Option> options;
		List<SearchDTO> searches = justWatchService.getFilteredSearches(parameters);
		if (searches.isEmpty()) {
			return getMessageError("Disculpa, no he encontrado ning??n resultado");
		} else {
			options = searches.stream().map(x -> new Option(x.getTitle(), x.getUrl(), x.getImage()))
					.collect(Collectors.toList());
		}
		return createOptionMessage("He encontrado los siguientes resultados, ??sobre cu??l querr??a m??s informaci??n?", "",
				options);
	}

	protected MessageDTO getFunctions(MessageResponse response, String userInput) {
		String text = "Las funciones que tengo implementadas actualmente son las siguientes:" + Constants.BR;
		List<Option> options = new ArrayList<>();
		List<Function> functions = functionService.findAll();
		for (Function function : functions) {
			text = text + Constants.BR + function.getDescription();
			options.add(new Option(function.getButton_label(), function.getButton_value(), null));
		}
		options.add(new Option("No quiero realizar ninguna acci??n actualmente",
				"No quiero realizar ninguna acci??n actualmente", null));
		text = text + Constants.BR + Constants.BR + "??Desea realizar alguna de estas acciones?";
		return createOptionMessage(text, "", options);
	}

	protected MessageDTO getActors(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = "Lo siento no he encontrado los actores";
		justWatchService.getMediaContent(parameters.get(Constants.URL_ACTUAL));
		Optional<MediaContent> mediaContent = mediaContentService
				.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContent.isPresent() && mediaContent.get().getImdbId() != null) {
			MediaContent mediaContentValue = mediaContent.get();
			res = getActorsMessage(TMDBService.getCastByMediaContent(mediaContentValue).getCast(), mediaContentValue);
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	protected MessageDTO getDirector(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = "Lo siento no he encontrado el director";
		justWatchService.getMediaContent(parameters.get(Constants.URL_ACTUAL));
		Optional<MediaContent> mediaContent = mediaContentService
				.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContent.isPresent() && mediaContent.get().getImdbId() != null) {
			MediaContent mediaContentValue = mediaContent.get();
			res = getPersonMessage(
					TMDBService.getDirector(TMDBService.getCastByMediaContent(mediaContentValue).getCrew()));
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	private String getPersonMessage(PersonDTO person) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String img = null;
		String title = null;
		String description = null;
		String textDiv = null;
		String res = "Lo siento no he encontrado resultados";
		if (person != null) {
			htmlAttributes.put(Constants.SRC, Constants.TMBD_IMAGE_BASE_URL + person.getProfile_path());
			img = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);
			htmlAttributes.clear();
			title = Utils.createHtmlTag(Constants.H1, person.getName(), htmlAttributes);
			description = Utils
					.createHtmlTag(Constants.P,
							person.getBiography() != null && !"".equals(person.getBiography()) ? person.getBiography()
									: "Lo siento, no he encontrado informaci??n relacionada con la biograf??a.",
							htmlAttributes);
			textDiv = Utils.createHtmlTag(Constants.DIV, title + Constants.BR + description, htmlAttributes);
			htmlAttributes.put(Constants.CLASS, Constants.PERSON_PROFILE);
			res = Utils.createHtmlTag(Constants.DIV, img + textDiv, htmlAttributes);
		}
		return res;
	}

	private String getActorsMessage(List<PersonDTO> castByMediaContent, MediaContent mediaContent) {
		String res = "Lo siento no he encontrado los actores";
		String img = "";
		String information = "";

		String listDiv = "";
		Map<String, String> htmlAttributes = new HashMap<>();
		List<PersonDTO> actors = castByMediaContent.stream().filter(x -> x.getOrder() != null && x.getOrder() < 12)
				.collect(Collectors.toList());
		if (!actors.isEmpty()) {
			res = Utils.createHtmlTag(Constants.P,
					"Los actores principales de " + mediaContent.getTitle() + " son:" + Constants.BR, new HashMap<>());
			for (PersonDTO personDTO : actors) {
				htmlAttributes.clear();
				htmlAttributes.put(Constants.SRC, Constants.TMBD_IMAGE_BASE_URL + personDTO.getProfile_path());

				img = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);

				htmlAttributes.clear();
				htmlAttributes.put(Constants.STYLE, Constants.BOLD);
				information = Utils.createHtmlTag(Constants.P, personDTO.getName(), htmlAttributes)
						+ Utils.createHtmlTag(Constants.P, "(" + personDTO.getCharacter() + ")", new HashMap<>());
				htmlAttributes.clear();
				htmlAttributes.put(Constants.CLASS, Constants.PERSON);
				listDiv = listDiv
						+ Utils.createHtmlTag(Constants.DIV, img + Constants.BR + information, htmlAttributes);
			}
			htmlAttributes.clear();
			htmlAttributes.put(Constants.CLASS, Constants.PERSON_LIST);
			res = res + Utils.createHtmlTag(Constants.DIV, listDiv, htmlAttributes);
		}
		return res;
	}

	protected MessageDTO getTrailer(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = "Lo siento no he encontrado ning??n tr??iler";
		justWatchService.getMediaContent(parameters.get(Constants.URL_ACTUAL));
		Optional<MediaContent> mediaContent = mediaContentService
				.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContent.isPresent() && mediaContent.get().getImdbId() != null) {
			MediaContent mediaContentValue = mediaContent.get();
			String urlTrailer = TMDBService.getTrailer(mediaContentValue);
			if (urlTrailer != null && !"".equals(urlTrailer)) {
				res = Utils.createHtmlTag(Constants.P,
						"Aqu?? tiene el tr??iler de " + mediaContentValue.getTitle() + ":" + Constants.BR,
						new HashMap<>());
				res = res + createYoutubeVideo(urlTrailer);
			}
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	protected MessageDTO addToWatchList(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = "??Ya tiene ese t??tulo en la lista!";
		justWatchService.getMediaContent(parameters.get(Constants.URL_ACTUAL));
		Optional<MediaContent> mediaContent = mediaContentService
				.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (!userService.existsOnToWatchList(mediaContent.get().getId())) {
			userService.addToWatchList(mediaContent.get());
			res = "He a??adido '" + mediaContent.get().getTitle() + "' a su lista";
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	protected MessageDTO getMyList(MessageResponse response, String userInput) {
		String res = "A??n no tienes t??tulos en tu lista";
		List<MediaContent> myList = userService.getByUserLogged().getToWatchList();
		if (!myList.isEmpty()) {
			res = "Los t??tulos de su lista son los siguientes:" + Constants.BR;
			res = res + createMyListMessage(myList);
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	private String createMyListMessage(List<MediaContent> myList) {
		HashMap<String, String> htmlAttributes = new HashMap<>();
		String res = "";
		String img;
		String information;
		String listDiv = "";
		for (MediaContent value : myList) {
			htmlAttributes.clear();
			if(value.getPoster() != null && !"".equals(value.getPoster())) {	
				htmlAttributes.put(Constants.SRC, value.getPoster());
			}else {
				htmlAttributes.put(Constants.SRC, "/assets/defaults/mediaContent.jpg");
			}

			img = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);

			htmlAttributes.clear();
			htmlAttributes.put(Constants.STYLE, Constants.BOLD);
			information = Utils.createHtmlTag(Constants.P, value.getTitle() + (value.getCreationDate() != null?" " + value.getCreationDate(): ""), htmlAttributes) + Utils.createHtmlTag(
					Constants.P, "(" + (value.getMediaType().name().equals(MediaType.MOVIE.name()) ? "Pel??cula" : "Serie") + ")",
					new HashMap<>());
			htmlAttributes.clear();
			htmlAttributes.put(Constants.CLASS, Constants.MY_LIST_ELEMENT);
			listDiv = listDiv + Utils.createHtmlTag(Constants.DIV, img + Constants.BR + information, htmlAttributes);
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.MY_LIST);
		res = res + Utils.createHtmlTag(Constants.DIV, listDiv, htmlAttributes);
		return res;
	}

	private String createYoutubeVideo(String urlTrailer) {
		return "<iframe width='560' height='315' src='https://www.youtube.com/embed/" + urlTrailer
				+ "' title='YouTube video player' frameborder='0' allow='accelerometer; autoplay; "
				+ "clipboard-write; encrypted-media; gyroscope; picture-in-picture' allowfullscreen></iframe>";
	}

	protected MessageDTO getGenres(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		String res = "";
		justWatchService.getMediaContent(parameters.get(Constants.URL_ACTUAL));
		Optional<MediaContent> mediaContent = mediaContentService
				.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContent.isPresent() && !mediaContent.get().getGenres().isEmpty()) {
			MediaContent mediaContentValue = mediaContent.get();
			res = "Los g??neros a los que pertenece " + mediaContentValue.getTitle() + " son los siguientes:"
					+ Constants.BR;
			List<String> genres = mediaContentValue.getGenres().stream().map(x -> x.getName()).distinct()
					.collect(Collectors.toList());
			res = res + createContentMappingMessage(genres);
		} else {
			res = "Lo siento no he encontrado informaci??n relacionada con los g??neros";
		}
		return new MessageDTO(res, SenderType.server.name(), false, null, false);
	}

	private String createContentMappingMessage(List<String> values) {
		HashMap<String, String> htmlAttributes = new HashMap<>();
		String p = "";
		String res = "";
		for (String value : values) {
			htmlAttributes.clear();
			p = Utils.createHtmlTag(Constants.P, value, htmlAttributes);
			htmlAttributes.put(Constants.CLASS, Constants.CONTENT_MAPPING);
			res = res + Utils.createHtmlTag(Constants.DIV, p, htmlAttributes);
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.CONTENT_MAPPING_LIST);
		res = Utils.createHtmlTag(Constants.DIV, res, htmlAttributes);
		return res;
	}

	protected MessageDTO getMediaContent(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		MediaContentDTO mediaContentDTO = justWatchService.getMediaContent((String) parameters.get(USER_INPUT));
		if (mediaContentDTO == null) {
			return getMessageError("No he conseguido obtener informaci??n sobre el contenido indicado");
		}
		return createMediaContentMessage(mediaContentDTO);
	}

	protected MessageDTO getPlatforms(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		List<Option> options = platformService.findAll().stream()
				.map(x -> new Option(x.getName(), x.getName(), x.getLogo())).collect(Collectors.toList());
		options.add(new Option("Me da igual la plataforma", "Me da igual la plataforma", null));
		return createOptionMessage(parameters.get("mensaje"), "", options);
	}

	protected MessageDTO getYears(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		List<Option> options = IntStream.rangeClosed(1900, Calendar.getInstance().get(Calendar.YEAR))
				.mapToObj(x -> new Option(String.valueOf(x), String.valueOf(x), null)).collect(Collectors.toList());
		options.add(new Option("Me da igual el a??o", "Me da igual el a??o", null));
		return createOptionMessage(parameters.get("mensaje"), "", options);
	}

	private MessageDTO createMediaContentMessage(MediaContentDTO mediaContentDTO) {
		String message = createImageMessage(mediaContentDTO.getPoster()) + Constants.BR + Constants.BR;
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);
		message = message + Utils.createHtmlTag(Constants.H2, mediaContentDTO.getTitle(), htmlAttributes);
		message = message + Utils.createHtmlTag(Constants.P, mediaContentDTO.getDescription(), new HashMap<>());

		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);

		String yearTitle = Utils.createHtmlTag(Constants.P, "A??o:&nbsp;", htmlAttributes);
		String yearValue = Utils.createHtmlTag(Constants.P, mediaContentDTO.getCreationDate(), new HashMap<>());

		String typeTitle = Utils.createHtmlTag(Constants.P, "Tipo de contenido:&nbsp;", htmlAttributes);
		String typeValue = Utils.createHtmlTag(Constants.P,
				mediaContentDTO.getMediaType().equals(MediaType.MOVIE.name()) ? "Pel??cula" : "Serie", new HashMap<>());

		htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT + Constants.WIDTH_100);

		message = message + Utils.createHtmlTag(Constants.DIV, Constants.BR + yearTitle + yearValue, htmlAttributes);
		if (mediaContentDTO.getScore() != null && !mediaContentDTO.getScore().trim().equals("")) {
			htmlAttributes.put(Constants.STYLE,
					Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);
			String scoreTitle = Utils.createHtmlTag(Constants.P, "Puntuaci??n:&nbsp;", htmlAttributes);
			String scoreValue = Utils.createHtmlTag(Constants.P, mediaContentDTO.getScore().trim(), new HashMap<>());
			htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT + Constants.WIDTH_100);
			message = message + Utils.createHtmlTag(Constants.DIV, scoreTitle + scoreValue, htmlAttributes);
		}
		message = message + Utils.createHtmlTag(Constants.DIV, typeTitle + typeValue, htmlAttributes);
		message = message + createPricesMessage(mediaContentDTO);

		return new MessageDTO(message, SenderType.server.name(), false, null, false);
	}

	private String createPricesMessage(MediaContentDTO mediaContentDTO) {
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);

		String priceTitle = Utils.createHtmlTag(Constants.H3, "Precios:", htmlAttributes);

		htmlAttributes.put(Constants.STYLE,
				Constants.FLOAT_LEFT + Constants.WIDTH_100 + Constants.MARGIN_BOTTOM_2_PERCENT);

		String res = Utils.createHtmlTag(Constants.DIV, Constants.BR + priceTitle, htmlAttributes);

		htmlAttributes.clear();
		htmlAttributes.put(Constants.STYLE, Constants.MARGIN_2_PERCENT);
		String noDisponible = Utils.createHtmlTag(Constants.P,
				"Actualmente no se encuentra disponible en ninguna plataforma", htmlAttributes);

		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE);
		htmlAttributes.put(Constants.ON_CLICK, Constants.COLLAPSIBLE_FUNCTION);

		String buttonStream = Utils.createHtmlTag(Constants.BUTTON, "Stream", htmlAttributes);
		String streamContent = "";
		if (!mediaContentDTO.getStream().isEmpty()) {
			for (PlatformWithPriceDTO price : mediaContentDTO.getStream()) {
				streamContent = streamContent + getPricesDiv(price);
			}
		} else {
			streamContent = noDisponible;
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE_CONTENT);
		String divStream = Utils.createHtmlTag(Constants.DIV, streamContent, htmlAttributes);
		htmlAttributes.clear();
		res = res + buttonStream + divStream;

		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE);
		htmlAttributes.put(Constants.ON_CLICK, Constants.COLLAPSIBLE_FUNCTION);

		String buttonRent = Utils.createHtmlTag(Constants.BUTTON, "Alquilar", htmlAttributes);
		String rentContent = "";
		if (!mediaContentDTO.getRent().isEmpty()) {
			for (PlatformWithPriceDTO price : mediaContentDTO.getRent()) {
				rentContent = rentContent + getPricesDiv(price);
			}
		} else {
			rentContent = noDisponible;
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE_CONTENT);
		String divRent = Utils.createHtmlTag(Constants.DIV, rentContent, htmlAttributes);
		htmlAttributes.clear();
		res = res + buttonRent + divRent;

		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE);
		htmlAttributes.put(Constants.ON_CLICK, Constants.COLLAPSIBLE_FUNCTION);

		String buttonBuy = Utils.createHtmlTag(Constants.BUTTON, "Comprar", htmlAttributes);
		String buyContent = "";
		if (!mediaContentDTO.getBuy().isEmpty()) {
			for (PlatformWithPriceDTO price : mediaContentDTO.getBuy()) {
				buyContent = buyContent + getPricesDiv(price);
			}
		} else {
			buyContent = noDisponible;
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.COLLAPSIBLE_CONTENT);
		String divBuy = Utils.createHtmlTag(Constants.DIV, buyContent, htmlAttributes);
		htmlAttributes.clear();
		res = res + buttonBuy + divBuy;
		return res;
	}

	private String getPricesDiv(PlatformWithPriceDTO price) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String cost = Utils.createHtmlTag(Constants.P, price.getCost(), htmlAttributes);
		String center = Utils.createHtmlTag(Constants.CENTER, cost, htmlAttributes);
		htmlAttributes.put(Constants.SRC, price.getLogo());
		htmlAttributes.put(Constants.ALT, price.getName());
		htmlAttributes.put(Constants.STYLE,
				Constants.BORDER_RADIUS_5PX + Constants.MARGIN_TOP_12PX + Constants.MARGIN_LEFT_12PX);
		String img = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);
		htmlAttributes.clear();
		htmlAttributes.put(Constants.TITLE, price.getName());
		htmlAttributes.put(Constants.HREF, price.getUrl());
		htmlAttributes.put(Constants.TARGET, Constants.BLANK);
		String a = Utils.createHtmlTag(Constants.A, img, htmlAttributes);
		htmlAttributes.clear();
		htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT);
		return Utils.createHtmlTag(Constants.DIV, a + center, htmlAttributes);
	}

	private MessageDTO getPremiereMessage(List<Premiere> premieres) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String mediaContentInformation;
		String message = Utils.createHtmlTag(Constants.P, "Los estrenos son:", new HashMap<>()) + Constants.BR;
		String type;
		Map<String, List<Premiere>> premieresByPlatform = new HashMap<>();
		premieres.stream().forEach(x -> {
			String platformName = x.getPlatform().getName();
			List<Premiere> premieresOnMap = new ArrayList<>();
			if (premieresByPlatform.containsKey(platformName)) {
				premieresOnMap = premieresByPlatform.get(platformName);
			}
			premieresOnMap.add(x);
			premieresByPlatform.put(platformName, premieresOnMap);
		});
		for (Entry<String, List<Premiere>> platform : premieresByPlatform.entrySet()) {
			htmlAttributes.clear();
			htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0 + Constants.BOLD);
			message = message + Utils.createHtmlTag(Constants.P, platform.getKey() + ":", htmlAttributes)
					+ Constants.BR;
			for (Premiere premiere : platform.getValue()) {
				htmlAttributes.clear();
				htmlAttributes.put(Constants.STYLE,
						Constants.MARGIN_0 + Constants.FONT_STYLE_ITALIC + Constants.TEXT_INDENT_20PX);
				type = "Pel??cula";
				mediaContentInformation = "";

				mediaContentInformation = mediaContentInformation + "-&nbsp;" + premiere.getMediaContent().getTitle();

				if ("SERIE".equals(premiere.getMediaContent().getMediaType().name())) {
					type = "Serie";
					mediaContentInformation = mediaContentInformation + " (" + premiere.getNews() + ", "
							+ premiere.getSeason() + ")";
				}
				mediaContentInformation = mediaContentInformation + " (" + type + ", "
						+ new SimpleDateFormat("dd/MM/yyyy").format(premiere.getPremiereDate()) + ")";
				message = message + Utils.createHtmlTag(Constants.P, mediaContentInformation, htmlAttributes)
						+ Constants.BR;

				htmlAttributes.clear();
				htmlAttributes.put(Constants.STYLE, Constants.DISPLAY_BLOCK + Constants.MARGIN_AUTO + Constants.WIDTH_60
						+ Constants.BORDER_RADIUS_5PX);
				htmlAttributes.put(Constants.SRC, premiere.getMediaContent().getPoster());
				message = message + Utils.createHtmlTag(Constants.IMG, "", htmlAttributes) + Constants.BR
						+ Constants.BR;
			}
		}
		return new MessageDTO(message, SenderType.server.name(), false, null, false);
	}

	private String createImageMessage(String source) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.SRC, source);
		htmlAttributes.put(Constants.STYLE,
				Constants.DISPLAY_BLOCK + Constants.MARGIN_AUTO + Constants.WIDTH_80 + Constants.BORDER_RADIUS_5PX);
		return Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);
	}

	private MessageDTO createWatsonOptionMessage(String title, String description,
			List<DialogNodeOutputOptionsElement> watsonOptions) {
		List<Option> options = new ArrayList<>();
		for (DialogNodeOutputOptionsElement watsonOption : watsonOptions) {
			options.add(new Option(watsonOption.getLabel(), watsonOption.getValue().getInput().text(), null));
		}

		return createOptionMessage(title, description, options);
	}

	private MessageDTO createOptionMessage(String title, String description, List<Option> options) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
		Selectable selectable = new Selectable(title, description, options);
		selectable = selectableService.save(selectable);
		List<OptionDTO> optionsDTO = options.stream().map(x -> new OptionDTO(x.getLabel(), x.getText(), x.getImage()))
				.collect(Collectors.toList());
		SelectableDTO selectableDTO = new SelectableDTO(selectable.getId(), optionsDTO);
		String html = Utils.createHtmlTag(Constants.P, title + description, htmlAttributes);

		return new MessageDTO(html, SenderType.server.name(), true, selectableDTO, false);
	}

	private MessageDTO getMessageError(String text) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
		String html = Utils.createHtmlTag(Constants.P, text, htmlAttributes);
		return new MessageDTO(html, SenderType.server.name(), false, null, false);
	}
	
	protected MessageDTO getReviews(MessageResponse response, String userInput) {
		Map<String, String> parameters = getParameters(response, userInput);
		MessageDTO res;
		Optional<MediaContent> mediaContentOptional = mediaContentService.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContentOptional.isPresent()) {
			MediaContent mediaContent = mediaContentOptional.get();
			List<ReviewDTO> reviews = reviewService.findByMediaContent(mediaContent);
			if (!reviews.isEmpty()) {
				res = getReviewsMessage(mediaContent, reviews);
			} else {
				res = watsonService.sendMessageNoSave(userService.getByUserLogged().getId(), new MessageDTO("###no_hay_reviews###", SenderType.user.name(), false, null, false));
			}
		} else {
			res = getMessageError("Lo siento, no puedo realizar la acci??n solicitada");
		}
		return res;
	}
	
	protected MessageDTO getTMDBReviews(MessageResponse response, String userInput){
		Map<String, String> parameters = getParameters(response, userInput);
		MessageDTO res = new MessageDTO("Lo siento no he encontrado opiniones de ese t??tulo en TMDB", SenderType.server.name(), false, null, false);
		Optional<MediaContent> mediaContentOptional = mediaContentService.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		if (mediaContentOptional.isPresent()) {
			MediaContent mediaContent = mediaContentOptional.get();
			List<ReviewDTO> reviews = TMDBService.getReviewsByMediaContent(mediaContent).getResults();
			if (!reviews.isEmpty()) {
				res = getReviewsMessage(mediaContent, reviews);
			}
		}
		return res;
	}
	
	private MessageDTO getReviewsMessage(MediaContent mediaContent, List<ReviewDTO> reviews) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String html;
		String reviewDivs = "";
		String avatar;
		String username;
		String content;
		String date;
		String ratingDiv;
		String stars;
		for (ReviewDTO reviewDTO : reviews) {
			htmlAttributes.clear();
			htmlAttributes.put(Constants.CLASS, Constants.REVIEW_AVATAR);
			String avatarUrl = reviewDTO.getAuthor_details().getAvatar_path();
			if(avatarUrl == null) {
				avatarUrl = "/assets/avatars/1.png";
			}else if(avatarUrl.startsWith("/https:")) {
				avatarUrl = avatarUrl.substring(1);
			}else if(reviewDTO.getRating() == null){
				avatarUrl = Constants.TMBD_IMAGE_BASE_URL + avatarUrl;
			}
			htmlAttributes.put(Constants.SRC, avatarUrl);
			avatar = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);
			username = Utils.createHtmlTag(Constants.H1, reviewDTO.getAuthor_details().getUsername(), new HashMap<>());
			content = Utils.createHtmlTag(Constants.P, reviewDTO.getContent(), new HashMap<>());
			htmlAttributes.clear();
			htmlAttributes.put(Constants.STYLE, Constants.FONT_STYLE_ITALIC);
			date = Utils.createHtmlTag(Constants.P, new SimpleDateFormat("dd/MM/yyyy").format(reviewDTO.getCreated_at()), htmlAttributes);
			
			if(reviewDTO.getRating() != null) {
				htmlAttributes.clear();
				htmlAttributes.put(Constants.SRC, "/assets/ratings/" + reviewDTO.getRating() + "stars.png");
				stars = Utils.createHtmlTag(Constants.IMG, "", htmlAttributes);
				htmlAttributes.clear();
				htmlAttributes.put(Constants.CLASS, Constants.REVIEW_RATING);
				ratingDiv = Utils.createHtmlTag(Constants.DIV, stars, htmlAttributes);
			} else {
				ratingDiv = "";
			}
			
			htmlAttributes.clear();
			htmlAttributes.put(Constants.CLASS, Constants.REVIEW);
			reviewDivs = reviewDivs + Utils.createHtmlTag(Constants.DIV, avatar + username + content + date + ratingDiv, htmlAttributes);
		}
		htmlAttributes.clear();
		htmlAttributes.put(Constants.CLASS, Constants.REVIEW_LIST);
		String title = Utils.createHtmlTag(Constants.P, "He encontrado las siguientes opiniones: ", new HashMap<>());
		html = Utils.createHtmlTag(Constants.DIV, title + Constants.BR + reviewDivs, new HashMap<>());
		return new MessageDTO(html, SenderType.server.name(), false, null, true);
	}
	
	protected MessageDTO saveReview(MessageResponse response, String userInput){
		Map<String, String> parameters = getParameters(response, userInput);
		Optional<MediaContent> mediaContentOptional = mediaContentService.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		User user = userService.getByUserLogged();
		MessageDTO res = new MessageDTO(); 
		try {
			if(mediaContentOptional.isPresent()) {
				MediaContent mediaContent = mediaContentOptional.get();
				if(reviewService.existsByMediaContentAndUserAndDraft(mediaContent, user, false)) {
					reviewService.save(new Review(new Date(), userInput, Integer.valueOf(parameters.get("rating").substring(0, 1)), user, mediaContent, true));
					List<Option> options = new ArrayList<Option>();
					options.add(new Option("Sobreescribir", "Sobreescribir", null));
					options.add(new Option("No sobreescribir", "No sobreescribir", null));
					res = createOptionMessage("Ya existe una valoraci??n suya para este t??tulo", " ??quiere sobreescribirla?", options);
				} else {
						reviewService.save(new Review(new Date(), userInput, Integer.valueOf(parameters.get("rating").substring(0, 1)), user, mediaContent, false));
						res = new MessageDTO("Se ha guardado correctamente su opini??n", SenderType.server.name(), false, null, false);
				}
			}
		} catch (Exception e){
			log.error("error al guardar la review", e);
			res = getMessageError("Disculpe, ha ocurrido un error al guardar la opini??n");
		}
		return res;
	}
	
	protected MessageDTO overwriteReview(MessageResponse response, String userInput){
		Map<String, String> parameters = getParameters(response, userInput);
		Optional<MediaContent> mediaContentOptional = mediaContentService.getByJustWatchUrl(parameters.get(Constants.URL_ACTUAL));
		User user = userService.getByUserLogged();
		MessageDTO res = new MessageDTO();
		try {
			if(mediaContentOptional.isPresent()) {
				MediaContent mediaContent = mediaContentOptional.get();
				reviewService.updateDraft(mediaContent, user);
				res = new MessageDTO("Se ha modificado correctamente su opini??n", SenderType.server.name(), false, null, false);
			}
		} catch (Exception e){
			log.error("error al modificar la review", e);
			res = getMessageError("Disculpe, ha ocurrido un error al modificar la opini??n");
		}
		return res;
	}
	
}
