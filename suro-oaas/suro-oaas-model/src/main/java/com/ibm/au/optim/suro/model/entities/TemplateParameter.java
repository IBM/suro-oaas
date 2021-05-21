/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>RunTemplateParameter</b>. This class extends {@link Parameter}
 * and adds an additional property that is used to provide information
 * about whether the value of the parameter can be changed.
 * 
 * @author Christian Vecchiola
 *
 */
public class TemplateParameter extends Parameter {

	/**
	 * A {@literal boolean} value indicating whether the value of the
	 * parameter can be changed by the user when creating a new optimization
	 * run out of this template. The default value is {@literal false}.
	 */
	@JsonProperty("fixed")
	protected boolean fixed = false;

	/**
	 * Initializes an instance of the {@link TemplateParameter} class with 
	 * the given name, and sets the value to {@liteal null} and the fixed attribute 
	 * to {@literal false}.
	 * 
	 * @param name	a {@link String} representing the name of the parameter it
	 * 				cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	public TemplateParameter(String name) {
		this(name, null, false);
	}

	/**
	 * Initializes an instance of the {@link TemplateParameter} class with 
	 * the given name and value. This constructur sets the fixed attribute to
	 * {@literal false}.
	 * 
	 * @param name	a {@link String} representing the name of the parameter it
	 * 				cannot be {@literal null} or an empty string.
	 * @param value	a {@link Object} representing the value bound to the 
	 * 				parameter.	
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	public TemplateParameter(String name, Object value) {
		this(name, value, false);
	}

	/**
	 * Initialises an instance of the {@link TemplateParameter} class with 
	 * the given values.
	 * 
	 * @param name		a {@link String} representing the name of the parameter 
	 * 					it cannot be {@literal null} or an empty string.
	 * @param value		a {@link Object} representing the value bound to the 
	 * 					parameter.	
	 * @param fixed 	a {@literal boolean} value that indicates whether the 
	 * 					parameter can be changed when creating a {@link Run}
	 * 					instance from this template.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	@JsonCreator
	public TemplateParameter(@JsonProperty("name") String name, @JsonProperty("value") Object value, @JsonProperty("fixed") boolean fixed) {
		super(name, value);
		
		this.setFixed(fixed);
	}
	
	/**
	 * Gets a {@literal boolean} value indicating whether the value of the
	 * parameter can be changed by the user when creating a new optimization
	 * run out of this template.
	 * 
	 * @return	{@literal true} if the parameter value cannot be changed, or
	 * 			{@literal false} otherwise. The default value is {@literal false}.
	 */
	public boolean isFixed() {
		
		return this.fixed;
	}
	
	/**
	 * Gets a {@literal boolean} value indicating whether the value of the
	 * parameter can be changed by the user when creating a new optimization
	 * run out of this template.
	 * 
	 * @param fixed	{@literal true} if the parameter value cannot be changed, 
	 * 				or {@literal false} otherwise. The default value is {@literal 
	 * 				false}.
	 */
	public void setFixed(boolean fixed) {
		
		this.fixed = fixed;
	}
	
	/**
	 * Gets a {@link String} representation of the {@link Parameter}.
	 * 
	 * @return a {@link String} in the following form: [name: <i>name</i>, value: <i>value</i>, fixed: <i>fixed</i>].
	 * 
	 */
	@Override
	public String toString() {
		
		return "[name: " + this.getName() + ", value: " + this.getValue() + ", fixed: " + this.isFixed() + "]";
	}
	
	/**
	 * This method clones an instance of {@link TemplateParameter}. The method 
	 * uses the super-class implementation of the method to clone the portion 
	 * of {@link TemplateParameter} that inherits from {@link Parameter}, then
	 * it completes the process with the properties specifically defined by the
	 * {@link TemplateParameter} class.
	 * 
	 * @return 	an instance of {@link TemplateParameter} whose property are either
	 * 			copied or cloned from the current instance.
	 */
	@Override
	public Parameter clone() {
		
		TemplateParameter zombie = (TemplateParameter) super.clone();
		zombie.setFixed(this.isFixed());
		
		return zombie;
	}
	
	/**
	 * Creates an instance of {@link TemplateParameter}. This method overrides
	 * the base-class version to change the type of the instance returned to
	 * {@link TemplateParameter}.
	 * 
	 * @return an instance of {@link TemplateParameter}.
	 */
	@Override
	protected Parameter newInstance() {
		
		return new TemplateParameter(this.getName());
	}
	
	
}
