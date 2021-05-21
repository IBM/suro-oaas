/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api.ws;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * Class <b>EnvironmentInjectorConfig</b>. This class extends {@link ServerEndpointConfig.Configurator}
 * and provide means to inject into the user properties of the {@link HandshakeRequest} instance received 
 * by the socket connection request an instance of the current {@link Environment} implementation that
 * is bound to the web application so that any component shared with the API is also available in the
 * context of the server web socket.
 * 
 * @author Christian Vecchiola
 *
 */
public class EnvironmentInjectorConfig extends ServerEndpointConfig.Configurator {
	
	/**
	 * A {@link Environment} instance that represent the implementation that is currently
	 * configured with the web application hosting the server web socket configured with
	 * instances of this class.
	 */
	protected Environment environment;
	
	/**
	 * Modifies the initial handshake with the client web socket request and injects the {@link Environment}
	 * implementation that is currently configured with the web application context. This is guaranteed to be
	 * not {@literal null} because it has been set up by the ComponentContextListener added to the
	 * web application context configuration.
	 * 
	 * @param config	a {@link ServerEndpointConfig} instance containing the configuration of the current
	 * 					client request.
	 * @param request	a {@link HandshakeRequest} instance representing the request for handshake issued by
	 * 					the client web socket.
	 * @param response	a {@link HandshakeResponse} instance representing the response to the handshake
	 * 					request being created by the server.
	 */
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
    	
    	
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        ServletContext context = httpSession.getServletContext();
        this.environment = (Environment) context.getAttribute(Environment.ENVIRONMENT_DEFAULT_INSTANCE);
        config.getUserProperties().put(Environment.ENVIRONMENT_DEFAULT_INSTANCE, environment);
        
    }
    
    /**
     * Gets the {@link Environment} implementation that is currently configured with the web application
     * hosting the server web socket that are configured with instances of this class.
     * 
     * @return	an {@link Environment} implementation or {@literal null}.
     */
	public Environment getEnvironment() {

		return this.environment;
	}
}
