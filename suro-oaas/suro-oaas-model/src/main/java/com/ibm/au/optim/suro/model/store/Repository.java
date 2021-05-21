/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store;

import com.ibm.au.optim.suro.model.entities.Attachment;

import java.util.List;

/**
 * Interface <i>Repository</i>. This interface defines the basic contract of a
 * repository for items of type {@link T}. The interface exposes the following
 * methods: 
 * <ul>
 * <li>{@link Repository#getAll()}: returns all the items in the repository.</li>
 * <li>{@link Repository#getItem(String)}: returns the item that corresponds to id.</li>
 * <li>{@link Repository#removeAll()}: removes all the items in the repository.</li>
 * <li>{@link Repository#removeItem(String)}: removes a specific item in the repository.</li>
 * </ul>
 * 
 * @author Christian Vecchiola
 */
public interface Repository<T> {

	/**
	 * Returns the collection of items that are in the repository.
	 * 
	 * @return	a {@link List} implementation that contains the collection of instances
	 * 			store in the repository.
	 */
	List<T> getAll();
	
	/**
	 * Returns the item that corresponds to the given <i>id</i>.
	 * 
	 * @param id	a {@link String} representing the unique identifier of the item.
	 * 				It cannot be {@literal null}.
	 * 
	 * @return  the instance that corresponds to <i>id</i> if found, {@literal null}
	 * 			otherwise.
	 * 
	 * @throws IllegalArgumentException	if <i>id</i> is {@literal null}.
	 */
	T getItem(String id);
	
	/**
	 * Adds the given item to the repository.
	 * 
	 * @param item	the item to add. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>item</i> is {@literal null}.
	 */
	void addItem(T item);

	
	/**
	 * Updates the given item to the repository.
	 * 
	 * @param item	the item to update. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>item</i> is {@literal null}.
	 */
	void updateItem(T item);

	/**
	 * Removes all the documents from the repository.
	 */
	void removeAll();

	/**
	 * Removes the given document from the repository.
	 * 
	 * @param id	the id of the item to remove. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>item</i> is {@literal null}.
	 */
	void removeItem(String id);

	/**
	 * Retrieves the collection of attachments that are associated to 
	 * the entity that is associated to the given <i>id</i>.
	 * 
	 * @param id	the id of the item whose attachments are queried
	 * 				for. It cannot be {@literal null}.
	 * 
	 * @return 	a {@link List} implementation containing the attachments
	 * 			associated to the item associated to the given <i>id</i>.
	 */
	List<Attachment> getAttachments(String id);

}
