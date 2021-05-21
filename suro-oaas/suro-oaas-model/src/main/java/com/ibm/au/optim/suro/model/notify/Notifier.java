/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.notify;

/**
 * Interface <b>Notifier</b>. This interface it is used by the {@link NotifierBus} component
 * which acts as a common communication bus for the application. This interface defines the
 * contract for any component wants to be notified by an event. 
 * 
 * See also:
 * <ul>
 * <li>{@link NotificationBus#subscribe(Notifier)}</li>
 * <li>{@link NotificationBus#unsubscribe(Notifier)}</li>
 * </ul>
 */
public interface Notifier {
	
	/**
	 * Notifies the instance about the occurrence of a given event.
	 * 
	 * @param message	a {@link String} representing the name of the event occurred.
	 * @param data		a {@link Object} containing the data attached to the event.
	 * 
	 * @throws NotifierException	in case any error occurs when processing the event.
	 */
	void notify(String event, Object data) throws NotifierException;
}
