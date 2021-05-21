/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.notify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Class <b>NotificationBus</b>. This class implements a simple bus that dispatch 
 * messages to a collection of listeners on the bus and implement the {@link Notifier}
 * interface. The bus instance uses a subscription model, where {@link Notifier}
 * instances subscribe and unsubscribe to the bus, and they will receive all the
 * messages that are dispatched by the bus, via a call to the bus instance method
 * {@link NotificationBus#broadcast(String, Object)}. 
 * </p>
 * <p>
 * The class implements the <i>singleton</i> pattern and there is only one shared
 * static instance, which is obtained via the static method {@link NotificationBus#getInstance()}.
 * </p>
 * 
 * @author Christian Vecchiola
 *
 */
public class NotificationBus {
	
	/**
	 * A {@link Logger} instance that collects all the messages that are produced
	 * by intstances of this class.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NotificationBus.class);

	/**
	 * A {@link List} implementation that provides access to and manages the
	 * list of {@link Notifier} instances currently subscribed to the bus.
	 */
	private List<Notifier> listeners = new CopyOnWriteArrayList<Notifier>();

	/**
	 * A {@link NotificationBus} instance representing the singleton that
	 * is returned by {@link NotificationBus#getInstance()}.
	 */
	private static final NotificationBus INSTANCE = new NotificationBus();

	/**
	 * Gets the shared instance of the {@link NotificationBus}.
	 * 
	 * @return the singleton.
	 */
	public static NotificationBus getInstance() {
		return INSTANCE;
	}

	/**
	 * Broadcast the given event to all the listeners subscribed to the bus.
	 * This method iterates over the list of {@link Notifier} implementations
	 * that are currently subscribed to the bus and invokes {@link Notifier#notify(String, Object)}
	 * if that execution generates a {@link NotifierException} it then removes
	 * the specific instance that generated the exception from the list of the
	 * subscribed notifiers.
	 * 
	 * @param event	a {@link String} representing the name of the event.
	 * @param data	a {@link Object} instance that represents the payload
	 * 				associated to the event.
	 */
	public void broadcast(String event, Object data) {
		
		for (Notifier notifier : this.listeners) {
			try {
				notifier.notify(event, data);
			
			} catch (NotifierException ne) {
				
				LOGGER.error("Error sending notification - Removed notifier " + (notifier != null ? notifier.toString() : "null") + ").", ne);
				this.listeners.remove(notifier);
			}
		}
	}
	
	/**
	 * Adds the given {@link Notifier} to the list of component that are
	 * listening for events on the bus.
	 * 
	 * @param notifier	a {@link Notifier} implementation.
	 */
	public void subscribe(Notifier notifier) {
		this.listeners.add(notifier);
	}

	/**
	 * Removes the given {@link Notifier} from the list of components that
	 * are listening for events on the bus.
	 * 
	 * @param notifier	a {@link Notifier} implementation.
	 */
	public void unsubscribe(Notifier notifier) {
		this.listeners.remove(notifier);
	}

	/**
	 * Checks whether the given {@link Notifier} is subscribed to the
	 * bus. This method returns {@literal true} if there has been a
	 * previous call to {@link NotificationBus#subscribe(Notifier)}
	 * with the same instance of {@link Notifier} and such instance
	 * has never thrown a {@link NotifierException} when processing
	 * events coming from the bus. 
	 * 
	 * @param notifier	a {@link Notifier} implementation.
	 * 
	 * @return  {@literal true} if the <i>notifier</i> subscribed and 
	 * 			it has always successfully processed bus events since
	 * 			then, {@literal false} if the <i>notifier</i> did never
	 * 			subscribed, or failed to process events broadcasted by
	 * 			the bus after subscribing, or simply unsubscribed itself.
	 */
	public boolean subscribed(Notifier notifier) {
		return listeners.contains(notifier);
	}

}
