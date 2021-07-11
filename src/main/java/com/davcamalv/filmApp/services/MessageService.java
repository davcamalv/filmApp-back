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

	private static final String START_P_TAG = "<p style='margin: 0 0 0; float: left;'>";
	private static final String END_P_TAG = "</p>";
	private static final String START_HR_TAG = "<tr>";
	private static final String END_HR_TAG = "</tr>";
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
			res = new MessageDTO(createTextMessage(text), SenderType.server.name(), false, null);
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
			res = new MessageDTO(
					"<p style='margin: 0 0 0'>Disculpe, actualmente no tengo implementada esa funcionalidad</p>",
					SenderType.server.name(), false, null);
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
		message = message + createBoldTitleMessage(mediaContentDTO.getTitle(), "h2") + "<br>";
		message = message + createTextMessage(mediaContentDTO.getDescription()) + "<br>";
		message = message + "<div>" + createBoldTitleMessage("Año: ", "p") + createTextMessage(" " + mediaContentDTO.getCreationDate()) + "</div>" + "<br>";
		if (mediaContentDTO.getScore() != null) {
			message = message + "<div>" + createBoldTitleMessage("Puntuación: ", "p") + createTextMessage(" " + mediaContentDTO.getScore().trim())
			+ "</div>" + "<br>";
		}
		message = message + "<div>" + createBoldTitleMessage("Tipo de contenido: ", "p")
				+ createTextMessage(mediaContentDTO.getMediaType().equals(MediaType.MOVIE.name()) ? " Película" : " Serie")
				+ "</div>" + "<br>";
		message = message + createPricesMessage(mediaContentDTO);
		return new MessageDTO(message, SenderType.server.name(), false, null);
	}

	private String createPricesMessage(MediaContentDTO mediaContentDTO) {
		String res = createBoldTitleMessage("Precios:", "h3") + "<br><br>"; 
		res = res + "<table style='width: 80%; margin: auto; border: 1px solid; border-collapse: collapse;'>";
		res = res + START_HR_TAG;
		res = res + "<th style='border: 1px solid;'><p style='writing-mode: vertical-lr;transform: rotate(180deg);'>Stream</p></th>";
		res = res + "<td style='border: 1px solid;'>";
		for (PlatformWithPriceDTO price : mediaContentDTO.getStream()) {
			res = res + getPricesTd(price);
		}
		res = res + "</td>";
		res = res + END_HR_TAG;

		res = res + START_HR_TAG;
		res = res + "<th style='border: 1px solid;'><p style='writing-mode: vertical-lr;transform: rotate(180deg);'>Alquilar</p></th>";
		res = res + "<td style='border: 1px solid;'>";

		for (PlatformWithPriceDTO price : mediaContentDTO.getRent()) {
			res = res + getPricesTd(price);
		}
		res = res + "</td>";
		res = res + END_HR_TAG;

		res = res + START_HR_TAG;
		res = res + "<th style='border: 1px solid;'><p style='writing-mode: vertical-lr;transform: rotate(180deg);'>Comprar</p></th>";
		res = res + "<td style='border: 1px solid;'>";
		for (PlatformWithPriceDTO price : mediaContentDTO.getBuy()) {
			res = res + getPricesTd(price);
		}
		res = res + "</td>";
		res = res + END_HR_TAG;
		res = res + "</table>";
		return res;
	}

	private String getPricesTd(PlatformWithPriceDTO streamPrice) {
		return "<div style='float: left;'><img style='border-radius: 5px;' alt='" + streamPrice.getName() + "' src='"
				+ streamPrice.getLogo() + "'/><center><p>" + streamPrice.getCost()
				+ "</p></center></div>";
	}
	
	private MessageDTO getPremiereMessage(List<Premiere> premieres) {
		String message = createTextMessage("Los estrenos son:") + "<br>";
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
			message = message + "<p style='margin: 0 0 0; font-weight: bold;'>" + platform.getKey() + ":</p><br>";
			for (Premiere premiere : platform.getValue()) {
				type = "Película";
				message = message + "<p style='margin: 0 0 0; font-style: italic; text-indent: 20px;'>" + "- "
						+ premiere.getMediaContent().getTitle();
				if ("SERIE".equals(premiere.getMediaContent().getMediaType().name())) {
					type = "Serie";
					message = message + " (" + premiere.getNews() + ", " + premiere.getSeason() + ")";
				}
				message = message + " (" + type + ", "
						+ new SimpleDateFormat("dd/MM/yyyy").format(premiere.getPremiereDate()) + ")" + "</p><br>";
				message = message + "<img style='display:block; margin:auto; width: 60%; border-radius: 5px' src="
						+ premiere.getMediaContent().getPoster() + " ><br><br>";
			}
		}
		return new MessageDTO(message, SenderType.server.name(), false, null);
	}

	private String createImageMessage(String source) {
		return "<img style='display:block; margin:auto; width: 80%; border-radius: 5px' src=" + source + " >";
	}

	private String createTextMessage(String text) {
		return START_P_TAG + text + END_P_TAG;
	}

	private String createBoldTitleMessage(String text, String size) {
		return "<" + size + " style='font-weight: bold; margin:auto; float: left;'>" + text + "</" + size + ">";
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
		Selectable selectable = new Selectable(title, description, options);
		selectable = selectableService.save(selectable);
		List<OptionDTO> optionsDTO = options.stream().map(x -> new OptionDTO(x.getLabel(), x.getText()))
				.collect(Collectors.toList());
		SelectableDTO selectableDTO = new SelectableDTO(selectable.getId(), optionsDTO);
		return new MessageDTO(START_P_TAG + title + description + "</p>", SenderType.server.name(), true,
				selectableDTO);
	}

	private MessageDTO getMessageError(String text) {
		return new MessageDTO(START_P_TAG + text + END_P_TAG, SenderType.server.name(), false, null);
	}
}
