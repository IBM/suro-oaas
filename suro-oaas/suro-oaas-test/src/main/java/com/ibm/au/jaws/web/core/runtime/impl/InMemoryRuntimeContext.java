/**
 * 
 */
package com.ibm.au.jaws.web.core.runtime.impl;

import java.util.Map;
import java.util.TreeMap;

import com.ibm.au.jaws.web.core.runtime.impl.AbstractRuntimeContext;

/**
 * Class <b>InMemoryRuntimeContext</b>. This class extends {@link AbstractRuntimeContext} and 
 * provides an implementation of the {@link RuntimeContext} interface that the utilises a 
 * simple {@link TreeMap} for storing the shared attributes.
 * 
 * @author Christian Vecchiola
 *
 */
public class InMemoryRuntimeContext extends AbstractRuntimeContext {
	
	/**
	 * A {@link Map} implementation that is used to store attributes.
	 */
	private Map<String,Object> ctx = null;
	
	/**
	 * This method releases the internal resources that are allocated
	 * to store the attributes. The method clears the internal map 
	 * and voids the reference.
	 */
	@Override
	protected void doRelease() {
		
		this.ctx.clear();
		this.ctx = null;
	}
	/**
	 * This method initialises the internal map. It recreates the 
	 * object if it was previously created.
	 */
	@Override
	protected void doBind() {
		
		this.ctx = new TreeMap<String,Object>();
		
	}
	

	/**
	 * This is abstract callback method delegates the task of retrieving the name of the attributes 
	 * that are exposed through this instance to inherited concrete classes. The invocation of this 
	 * method occurs within a synchronised context and with the precondition that the instance is 
	 * bound (i.e. {@literal RuntimeContext#isBound()} returns {@literal true}).
	 * 
	 * @return	a non {@literal null} array that contains the name of the attributes exposed by this
	 * 			instance.
	 */
	@Override
	protected String[] doGetAttributes() {
		
		String[] names = null;
		int size =  ctx.size();
		names = new String[size];
		int index = 0;
		for(String name : ctx.keySet()) {
			names[index++] = name;
		}
		
		return names;
	}
	
	/**
	 * This abstract callback method delegates the task of retrieving the selected attribute to the
	 * inherited concrete classes. The invocation of this  method occurs within a synchronised context 
	 * and with the preconditions that the instance is bound (i.e. {@literal RuntimeContext#isBound()} 
	 * returns {@literal true}) and the argument <i>name</i> is not {@literal null}.
	 * 
	 * @param name	a {@link String} representing the name of the attribute to retrieve. The assumption
	 * 				is that this argument is not {@literal null}.
	 * 				
	 * @return	the value of the attribute identified by <i>name</i> or {@literal null} if not found.
	 */
	@Override
	protected Object doGetAttribute(String name) {
	
		return this.ctx.get(name);
	}
	
	/**
	 * This abstract callback method delegates the task of setting the value of the selected attribute
	 * to the inherited concrete classes. The invocation of this  method occurs within a synchronised 
	 * context and with the preconditions that the instance is bound (i.e. {@literal RuntimeContext#isBound()} 
	 * returns {@literal true}) and the argument <i>name</i> is not {@literal null}.
	 * 
	 * @param name	a {@link String} representing the name of the attribute to retrieve. The assumption
	 * 				is that this argument is not {@literal null}.
	 * 
	 * @param value an {@link Object} instance representing the value to be set for <i>name</i>.
	 */
	@Override
	protected void doSetAttribute(String name, Object value) {
		
		this.ctx.put(name, value);
	}
	
	/**
	 * This abstract callback method delegats the task of removing the selected attributed to the inherited
	 * concrete classes. The invocation of this  method occurs within a synchronised context and with the 
	 * preconditions that the instance is bound (i.e. {@literal RuntimeContext#isBound()} returns {@literal 
	 * true}) and the argument <i>name</i> is not {@literal null}.
	 * 
	 * @param name	a {@link String} representing the name of the attribute to retrieve. The assumption
	 * 				is that this argument is not {@literal null}.
	 */
	@Override
	protected void doRemoveAttribute(String name) {
		
		this.ctx.remove(name);
	}
	
}
