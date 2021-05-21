/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api.ws;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.au.optim.suro.model.notify.Notifier;
import com.ibm.au.optim.suro.model.notify.NotifierException;

/**
 * Class <b>WebSocketNotifierAdapter</b>. This class implements the {@link Notifier} interface
 * and it is used to relay the information received from the notification bus onto a websocket
 * session.
 */
public class WebSocketNotifierAdapter implements Notifier {

	/**
	 * A {@link Logger} instance that records all the messages produced by instances of this
	 * class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebSocketNotifierAdapter.class);

	/**
	 * A {@link Session} instance that ties the adapter to the target websocket session.
	 */
	private Session session;

	/**
	 * Initialises an instance of the {@link WebSocketNotifierAdapter} type and configures
	 * it with the given websocket session.
	 * 
	 * @param session 	a {@link Session} instance representing the websocket session this
	 * 					instance of the adapter is configured with.
	 */
	public WebSocketNotifierAdapter(Session session) {
		this.session = session;
	}

	/**
	 * Notification callback. This method relays the data passed as argument onto the websocket
	 * session. The method expects that <i>data</i> can be converted to a {@link String} by
	 * invoking {@link Object#toString()} and creates a JSON object structured as follows:
	 * <pre>
	 *   { message : "data" }
	 * </pre>
	 * 
	 * @param event	a {@link String} representing the name of the event. This will be mapped
	 * 					to the name of the property of the JSON object sent through the websocket
	 * 					session. It cannot be {@literal null}.
	 * @param data		an {@link Object} instance containing the data associated to the event. 
	 * 					This will be mapped to the value of the property named <i>message</i>
	 * 					after being converted to {@link String}. It cannot be {@literal null}.
	 * 
	 * @throws NotifierException	if any error occurs while constructing and sending the message
	 * 								through the attached websocket session.
	 */
	public void notify(String event, Object data) throws NotifierException {
		
		try {
			StringBuilder sb = new StringBuilder();
			
			String message = sb.append("{\"").append(event).append("\":").append(data.toString()).append("}").toString();

			logger.info(String.format("[%s] WS Message Sent [message: %s]", session.getId(), message));
			session.getBasicRemote().sendText(message);
		
		} catch (Exception e) {
			
			throw new NotifierException(String.format("Error sending message to WebSocket [%s]", session.getId()), e);
		}

	}
}
