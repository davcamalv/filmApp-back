package com.davcamalv.filmApp.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Message;
import com.davcamalv.filmApp.domain.Option;
import com.davcamalv.filmApp.domain.Platform;
import com.davcamalv.filmApp.domain.Premiere;
import com.davcamalv.filmApp.domain.Selectable;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.MediaContentDTO;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.davcamalv.filmApp.dtos.OptionDTO;
import com.davcamalv.filmApp.dtos.PlatformWithPriceDTO;
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

	public List<MessageDTO> findMessagesByUser(int pageNumber, int pageSize, User user) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
		List<MessageDTO> res = messageRepository.findByUser(pageable, user).stream()
				.map(x -> new MessageDTO(x.getMessage(), x.getSender().name(), false, null))
				.collect(Collectors.toList());
		Collections.reverse(res);
		return res;
	}

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
			htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
			String html = Utils.createHtmlTag(Constants.P, text, htmlAttributes);
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
			htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
			String html = Utils.createHtmlTag(Constants.P,
					"Disculpe, actualmente no tengo implementada esa funcionalidad", htmlAttributes);
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
		String message = createImageMessage(mediaContentDTO.getPoster()) + Constants.BR + Constants.BR;
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);
		message = message + Utils.createHtmlTag(Constants.H2, mediaContentDTO.getTitle(), htmlAttributes);
		message = message + Utils.createHtmlTag(Constants.P, mediaContentDTO.getDescription(), new HashMap<>());

		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);

		String yearTitle = Utils.createHtmlTag(Constants.P, "Año:&nbsp;", htmlAttributes);
		String yearValue = Utils.createHtmlTag(Constants.P, mediaContentDTO.getCreationDate(), new HashMap<>());

		String typeTitle = Utils.createHtmlTag(Constants.P, "Tipo de contenido:&nbsp;", htmlAttributes);
		String typeValue = Utils.createHtmlTag(Constants.P,
				mediaContentDTO.getMediaType().equals(MediaType.MOVIE.name()) ? "Película" : "Serie", new HashMap<>());

		htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT + Constants.WIDTH_100);

		message = message + Utils.createHtmlTag(Constants.DIV, Constants.BR + yearTitle + yearValue, htmlAttributes);
		if (mediaContentDTO.getScore() != null && !mediaContentDTO.getScore().trim().equals("")) {
			htmlAttributes.put(Constants.STYLE,
					Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);
			String scoreTitle = Utils.createHtmlTag(Constants.P, "Puntuación:&nbsp;", htmlAttributes);
			String scoreValue = Utils.createHtmlTag(Constants.P, mediaContentDTO.getScore().trim(), new HashMap<>());
			htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT + Constants.WIDTH_100);
			message = message + Utils.createHtmlTag(Constants.DIV, scoreTitle + scoreValue, htmlAttributes);
		}
		message = message + Utils.createHtmlTag(Constants.DIV, typeTitle + typeValue, htmlAttributes);
		message = message + createPricesMessage(mediaContentDTO);
		return new MessageDTO(message, SenderType.server.name(), false, null);
	}

	private String createPricesMessage(MediaContentDTO mediaContentDTO) {
		HashMap<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE,
				Constants.BOLD + Constants.WIDTH_100 + Constants.FLOAT_LEFT + Constants.MARGIN_AUTO);

		String priceTitle = Utils.createHtmlTag(Constants.H3, "Precios:", htmlAttributes);

		htmlAttributes.put(Constants.STYLE, Constants.FLOAT_LEFT + Constants.WIDTH_100);

		String res = Utils.createHtmlTag(Constants.DIV, Constants.BR + priceTitle, htmlAttributes);
		String tableContent = "";
		htmlAttributes.put(Constants.STYLE, Constants.ROTATE_TEXT);
		String pStream = Utils.createHtmlTag(Constants.P, "Stream", htmlAttributes);
		htmlAttributes.put(Constants.STYLE, Constants.BORDER_SOLID_1PX);
		String thStream = Utils.createHtmlTag(Constants.TH, pStream, htmlAttributes);
		String tdStreamContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getStream()) {
			tdStreamContent = tdStreamContent + getPricesTd(price);
		}
		String tdStream = Utils.createHtmlTag(Constants.TD, tdStreamContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag(Constants.TR, thStream + tdStream, htmlAttributes);

		htmlAttributes.put(Constants.STYLE, Constants.ROTATE_TEXT);
		String pRent = Utils.createHtmlTag(Constants.P, "Alquilar", htmlAttributes);
		htmlAttributes.put(Constants.STYLE, Constants.BORDER_SOLID_1PX);
		String thRent = Utils.createHtmlTag(Constants.TH, pRent, htmlAttributes);
		String tdRentContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getRent()) {
			tdRentContent = tdRentContent + getPricesTd(price);
		}
		String tdRent = Utils.createHtmlTag(Constants.TD, tdRentContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag(Constants.TR, thRent + tdRent, htmlAttributes);

		htmlAttributes.put(Constants.STYLE, Constants.ROTATE_TEXT);
		String pBuy = Utils.createHtmlTag(Constants.P, "Comprar", htmlAttributes);
		htmlAttributes.put(Constants.STYLE, Constants.BORDER_SOLID_1PX);
		String thBuy = Utils.createHtmlTag(Constants.TH, pBuy, htmlAttributes);
		String tdBuyContent = "";
		for (PlatformWithPriceDTO price : mediaContentDTO.getBuy()) {
			tdBuyContent = tdBuyContent + getPricesTd(price);
		}
		String tdBuy = Utils.createHtmlTag(Constants.TD, tdBuyContent, htmlAttributes);
		htmlAttributes.clear();
		tableContent = tableContent + Utils.createHtmlTag(Constants.TR, thBuy + tdBuy, htmlAttributes);

		htmlAttributes.clear();
		htmlAttributes.put(Constants.STYLE, Constants.WIDTH_80 + Constants.MARGIN_AUTO + Constants.BORDER_SOLID_1PX
				+ Constants.BORDER_COLLAPSE_COLLAPSE);
		res = res + Utils.createHtmlTag(Constants.TABLE, tableContent, htmlAttributes);
		return res;
	}

	private String getPricesTd(PlatformWithPriceDTO price) {
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
		return new MessageDTO(message, SenderType.server.name(), false, null);
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
			options.add(new Option(watsonOption.getLabel(), watsonOption.getValue().getInput().text()));
		}

		return createOptionMessage(title, description, options);
	}

	private MessageDTO createOptionMessage(String title, String description, List<Option> options) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
		Selectable selectable = new Selectable(title, description, options);
		selectable = selectableService.save(selectable);
		List<OptionDTO> optionsDTO = options.stream().map(x -> new OptionDTO(x.getLabel(), x.getText()))
				.collect(Collectors.toList());
		SelectableDTO selectableDTO = new SelectableDTO(selectable.getId(), optionsDTO);
		String html = Utils.createHtmlTag(Constants.P, title + description, htmlAttributes);

		return new MessageDTO(html, SenderType.server.name(), true, selectableDTO);
	}

	private MessageDTO getMessageError(String text) {
		Map<String, String> htmlAttributes = new HashMap<>();
		htmlAttributes.put(Constants.STYLE, Constants.MARGIN_0);
		String html = Utils.createHtmlTag(Constants.P, text, htmlAttributes);
		return new MessageDTO(html, SenderType.server.name(), false, null);
	}
}
