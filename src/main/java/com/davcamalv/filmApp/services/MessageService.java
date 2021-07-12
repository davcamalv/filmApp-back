package com.davcamalv.filmApp.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Message;
import com.davcamalv.filmApp.domain.Option;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.domain.Selectable;
import com.davcamalv.filmApp.dtos.MediaContentDTO;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.davcamalv.filmApp.dtos.OptionDTO;
import com.davcamalv.filmApp.dtos.PlatformWithPriceDTO;
import com.davcamalv.filmApp.dtos.SearchDTO;
import com.davcamalv.filmApp.dtos.SelectableDTO;
import com.davcamalv.filmApp.enums.MediaType;
import com.davcamalv.filmApp.enums.SenderType;
import com.davcamalv.filmApp.repositories.MessageRepository;
import com.davcamalv.filmApp.utils.Utils;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

@Service
public class MessageService {

	private static final String STYLE = "style";
	private static final String BOLD_STYLE = "font-weight: bold; margin:auto; float: left;";
	private static final String MARGIN_ZERO = "margin: 0 0 0;";
	private static final String FLOAT_LEFT_100_STYLE = "width: 100%;float: left;";
	private static final String ROTATE_TEXT_STYLE = "writing-mode: vertical-lr;transform: rotate(180deg);";
	private static final String PX_SOLID_BORDER = "border: 1px solid;";
	private static final String USER_INPUT = "userInput";
	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SelectableService selectableService;

	@Autowired
	private UserService userService;

	@Autowired
	private PlatformService platformService;

	@Autowired
	private PremiereService premiereService;

	@Autowired
	private JustWatchService justWatchService;

	public Message saveUserMessage(MessageDTO messageDTO) {
		Message message = new Message(Utils.makeSafeMessage(messageDTO.getMessage()),
				SenderType.valueOf(messageDTO.getSender()), userService.getByUserLogged(), false, null);
		return messageRepository.save(message);
	}

	public Message save(MessageDTO messageDTO, Selectable selectable) {
		Message message = new Message(messageDTO.getMessage(), SenderType.valueOf(messageDTO.getSender()),
				userService.getByUserLogged(), messageDTO.getSpecialKeyboard(), selectable);
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
			res = new MessageDTO(createImageMessage(source), SenderType.server.name(), false, null);
			break;
		case "option":
			String title = output.title();
			String description = output.description();
			List<DialogNodeOutputOptionsElement> options = output.options();
			res = createWatsonOptionMessage(title, description, options);
			break;
		default:
			String text = Utils.makeSafeMessage(output.text());
			htmlAttributes.put(STYLE, MARGIN_ZERO);
			String html = Utils.createHtmlTag("p", text, htmlAttributes);
			res = new MessageDTO(html, SenderType.server.name(), false, null);
			break;
		}
		return res;
	}

	private MessageDTO processResponseByReflection(MessageResponse response, MessageDTO userMessage) {
		MessageDTO res;
		try {
			String methodName = response.getOutput().getActions().get(0).getName();
			Map<String, Object> parameters = response.getOutput().getActions().get(0).getParameters();
			parameters.put(USER_INPUT, userMessage.getMessage());
			Method method = getClass().getDeclaredMethod(methodName, Map.class);
			res = (MessageDTO) method.invoke(this, parameters);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			Map<String, String> htmlAttributes = new HashMap<>();
			htmlAttributes.put(STYLE, MARGIN_ZERO);
			String html = Utils.createHtmlTag("p", "Disculpe, actualmente no tengo implementada esa funcionalidad",
					htmlAttributes);
			res = new MessageDTO(html, SenderType.server.name(), false, null);
		}
		return res;
	}

	protected MessageDTO getPremieres(Map<String, Object> params) throws ParseException {
		Date date = null;
		String dateStr = (String) params.get("date");
		String platformStr = (String) params.get("provider");
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

	protected MessageDTO getSearches(Map<String, Object> params) {
		String title = (String) params.get(USER_INPUT);
		List<Option> options;
		List<SearchDTO> searches = justWatchService.getSearches(title);
		if (searches.isEmpty()) {
			return getMessageError("Disculpa, no he encontrado ningún resultado");
		} else {
			options = searches.stream().map(x -> new Option(x.getTitle() + " " + x.getYear(), x.getUrl()))
					.collect(Collectors.toList());
		}
		return createOptionMessage("¿A cuál de los siguientes resultados se refiere?", "", options);
	}

	protected MessageDTO getMediaContent(Map<String, Object> params) {
		MediaContentDTO mediaContentDTO = justWatchService.getMediaContent((String) params.get(USER_INPUT));
		if (mediaContentDTO == null) {
			return getMessageError("No he conseguido obtener información sobre el contenido indicado");
		}
		return createMediaContentMessage(mediaContentDTO);
	}

	private MessageDTO createMediaContentMessage(MediaContentDTO mediaContentDTO) {
		String message = createImageMessage(mediaContentDTO.getPoster()) + "<br><br>";
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(STYLE, BOLD_STYLE + "width: 100%;");
		message = message + Utils.createHtmlTag("h2", mediaContentDTO.getTitle(), htmlAttributes);
		message = message + Utils.createHtmlTag("p", mediaContentDTO.getDescription(), new HashMap<>());
		
		htmlAttributes.put(STYLE, BOLD_STYLE);

		String yearTitle = Utils.createHtmlTag("p", "Año:&nbsp;", htmlAttributes);
		String yearValue = Utils.createHtmlTag("p", mediaContentDTO.getCreationDate(), new HashMap<>());

		String typeTitle = Utils.createHtmlTag("p", "Tipo de contenido:&nbsp;", htmlAttributes);
		String typeValue = Utils.createHtmlTag("p",
				mediaContentDTO.getMediaType().equals(MediaType.MOVIE.name()) ? "Película" : "Serie", new HashMap<>());

		htmlAttributes.put(STYLE, FLOAT_LEFT_100_STYLE);

		message = message + Utils.createHtmlTag("div", "<br>" + yearTitle + yearValue, htmlAttributes);
		if (mediaContentDTO.getScore() != null && !mediaContentDTO.getScore().trim().equals("")) {
			htmlAttributes.put(STYLE, BOLD_STYLE);
			String scoreTitle = Utils.createHtmlTag("p", "Puntuación:&nbsp;", htmlAttributes);
			String scoreValue = Utils.createHtmlTag("p", mediaContentDTO.getScore().trim(), new HashMap<>());
			htmlAttributes.put(STYLE, FLOAT_LEFT_100_STYLE);
			message = message + Utils.createHtmlTag("div", scoreTitle + scoreValue, htmlAttributes);
		}
		message = message + Utils.createHtmlTag("div", typeTitle + typeValue, htmlAttributes);
		message = message + createPricesMessage(mediaContentDTO);
		return new MessageDTO(message, SenderType.server.name(), false, null);
	}

	private String createPricesMessage(MediaContentDTO mediaContentDTO) {
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(STYLE, BOLD_STYLE);

		String priceTitle = Utils.createHtmlTag("h3", "Precios:", htmlAttributes);

		htmlAttributes.put(STYLE, FLOAT_LEFT_100_STYLE);

		String res = Utils.createHtmlTag("div", "<br>" + priceTitle, htmlAttributes);
		String tableContent = "";
		htmlAttributes.put(STYLE, ROTATE_TEXT_STYLE);
		String pStream = Utils.createHtmlTag("p", "Stream", htmlAttributes);
		htmlAttributes.put(STYLE, PX_SOLID_BORDER);
		String thStream = Utils.createHtmlTag("th", pStream, htmlAttributes);
		String tdStreamContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getStream()) {
			tdStreamContent = tdStreamContent + getPricesTd(price);
		}
		String tdStream = Utils.createHtmlTag("td", tdStreamContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag("tr", thStream + tdStream, htmlAttributes);

		htmlAttributes.put(STYLE, ROTATE_TEXT_STYLE);
		String pRent = Utils.createHtmlTag("p", "Alquilar", htmlAttributes);
		htmlAttributes.put(STYLE, PX_SOLID_BORDER);
		String thRent = Utils.createHtmlTag("th", pRent, htmlAttributes);
		String tdRentContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getRent()) {
			tdRentContent = tdRentContent + getPricesTd(price);
		}
		String tdRent = Utils.createHtmlTag("td", tdRentContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag("tr", thRent + tdRent, htmlAttributes);

		htmlAttributes.put(STYLE, ROTATE_TEXT_STYLE);
		String pBuy = Utils.createHtmlTag("p", "Comprar", htmlAttributes);
		htmlAttributes.put(STYLE, PX_SOLID_BORDER);
		String thBuy = Utils.createHtmlTag("th", pBuy, htmlAttributes);
		String tdBuyContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getBuy()) {
			tdBuyContent = tdBuyContent + getPricesTd(price);
		}
		String tdBuy = Utils.createHtmlTag("td", tdBuyContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag("tr", thBuy + tdBuy, htmlAttributes);

		htmlAttributes.clear();
		htmlAttributes.put(STYLE, "width: 80%; margin: auto; border: 1px solid; border-collapse: collapse;");
		res = res + Utils.createHtmlTag("table", tableContent, htmlAttributes);
		return res;
	}

	private String getPricesTd(PlatformWithPriceDTO price) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String cost = Utils.createHtmlTag("p", price.getCost(), htmlAttributes);
		String center = Utils.createHtmlTag("center", cost, htmlAttributes);
		htmlAttributes.put("src", price.getLogo());
		htmlAttributes.put("alt", price.getName());
		htmlAttributes.put(STYLE, "border-radius: 5px; margin-top: 12px; margin-left: 12px;price.getLogo()");
		String img = Utils.createHtmlTag("img", "", htmlAttributes);
		htmlAttributes.clear();
		htmlAttributes.put("title", price.getName());
		htmlAttributes.put("href", price.getUrl());
		htmlAttributes.put("target", "_blank");
		String a = Utils.createHtmlTag("a", img, htmlAttributes);
		htmlAttributes.clear();
		htmlAttributes.put(STYLE, "float: left;");
		return Utils.createHtmlTag("div", a + center, htmlAttributes);
	}

	private MessageDTO getPremiereMessage(List<Premiere> premieres) {
		Map<String, String> htmlAttributes = new HashMap<>();
		String mediaContentInformation;
		String message = Utils.createHtmlTag("p", "Los estrenos son:", new HashMap<>()) + "<br>";
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
			htmlAttributes.put(STYLE, "margin: 0 0 0; font-weight: bold;");
			message = message + Utils.createHtmlTag("p", platform.getKey() + ":", htmlAttributes) + "<br>";
			for (Premiere premiere : platform.getValue()) {
				htmlAttributes.clear();
				htmlAttributes.put(STYLE, "margin: 0 0 0; font-style: italic; text-indent: 20px;");
				type = "Película";
				mediaContentInformation = "";

				mediaContentInformation = mediaContentInformation + "-&nbsp;" + premiere.getMediaContent().getTitle();

				if ("SERIE".equals(premiere.getMediaContent().getMediaType().name())) {
					type = "Serie";
					mediaContentInformation = mediaContentInformation + " (" + premiere.getNews() + ", "
							+ premiere.getSeason() + ")";
				}
				mediaContentInformation = mediaContentInformation + " (" + type + ", "
						+ new SimpleDateFormat("dd/MM/yyyy").format(premiere.getPremiereDate()) + ")";
				message = message + Utils.createHtmlTag("p", mediaContentInformation, htmlAttributes) + "<br>";

				htmlAttributes.clear();
				htmlAttributes.put(STYLE, "display:block; margin:auto; width: 60%; border-radius: 5px");
				htmlAttributes.put("src", premiere.getMediaContent().getPoster());
				message = message + Utils.createHtmlTag("img", "", htmlAttributes) + "<br><br>";
			}
		}
		return new MessageDTO(message, SenderType.server.name(), false, null);
	}

	private String createImageMessage(String source) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put("src", source);
		htmlAttributes.put(STYLE, "display:block; margin:auto; width: 80%; border-radius: 5px;");
		return Utils.createHtmlTag("img", "", htmlAttributes);
	}

	private MessageDTO createWatsonOptionMessage(String title, String description,
			List<DialogNodeOutputOptionsElement> watsonOptions) {
		List<Option> options = new ArrayList<>();
		for (DialogNodeOutputOptionsElement watsonOption : watsonOptions) {
			options.add(new Option(watsonOption.getLabel(), watsonOption.getValue().getInput().text()));
		}

		return createOptionMessage(title, description, options);
	}

	private MessageDTO createOptionMessage(String title, String description, List<Option> options) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(STYLE, MARGIN_ZERO);
		Selectable selectable = new Selectable(title, description, options);
		selectable = selectableService.save(selectable);
		List<OptionDTO> optionsDTO = options.stream().map(x -> new OptionDTO(x.getLabel(), x.getText()))
				.collect(Collectors.toList());
		SelectableDTO selectableDTO = new SelectableDTO(selectable.getId(), optionsDTO);
		String html = Utils.createHtmlTag("p", title + description, htmlAttributes);

		return new MessageDTO(html, SenderType.server.name(), true, selectableDTO);
	}

	private MessageDTO getMessageError(String text) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(STYLE, MARGIN_ZERO);
		String html = Utils.createHtmlTag("p", text, htmlAttributes);
		return new MessageDTO(html, SenderType.server.name(), false, null);
	}
}
