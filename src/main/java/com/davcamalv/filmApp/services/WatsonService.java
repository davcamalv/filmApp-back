package com.davcamalv.filmApp.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davcamalv.filmApp.domain.Session;
import com.davcamalv.filmApp.domain.User;
import com.davcamalv.filmApp.dtos.MessageDTO;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;

@Service
public class WatsonService {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	public Assistant getAssistant() {
		Authenticator authenticator = new IamAuthenticator(
				configurationService.getByProperty("watson.apikey").getValue());
		Assistant assistant = new Assistant(configurationService.getByProperty("watson.version.date").getValue(),
				authenticator);
		assistant.setServiceUrl(configurationService.getByProperty("watson.service.url").getValue());
		return assistant;
	}

	public String createSession(Long userId) {
		User user = userService.findOne(userId).get();
		String assistantId = configurationService.getByProperty("watson.assistant.id").getValue();
		Session session = sessionService.getSessionByUser(user);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, -4);
		Assistant assistant = getAssistant();
		SessionResponse watsonSession = null;
		CreateSessionOptions createSessionOptions = null;
		String sessionId = "";
		Date dateToRefresh = calendar.getTime();
		if (session != null) {
			if (session.getDate().before(dateToRefresh)) {
				createSessionOptions = new CreateSessionOptions.Builder(assistantId).build();
				watsonSession = assistant.createSession(createSessionOptions).execute().getResult();
				sessionId = watsonSession.getSessionId();
				session.setDate(new Date());
				session.setWatsonSession(sessionId);
			} else {
				session.setDate(new Date());
				sessionId = session.getWatsonSession();
			}
		} else {
			createSessionOptions = new CreateSessionOptions.Builder(assistantId).build();
			watsonSession = assistant.createSession(createSessionOptions).execute().getResult();
			sessionId = watsonSession.getSessionId();
			session = new Session();
			session.setDate(new Date());
			session.setUser(user);
			session.setWatsonSession(sessionId);
		}
		sessionService.save(session);
		return sessionId;
	}

	public String sendMessage(Long idUsuario, MessageDTO message) {
		String assistantId = configurationService.getByProperty("watson.assistant.id").getValue();
		Assistant assistant = getAssistant();
		String sessionId = createSession(idUsuario);
		MessageInput input = new MessageInput.Builder().messageType("text").text(message.getMessage()).build();
		MessageOptions messageOptions = new MessageOptions.Builder(assistantId, sessionId).input(input).build();
		MessageResponse response = assistant.message(messageOptions).execute().getResult();
		return response.getOutput().getGeneric().get(0).text();
	}
}
