/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>Parameter</b>. A parameter is a simple mapping between a name
 * and a value. This is the base class of all the different types of parameters
 * used in the application.
 * 
 * @see {@link TemplateParameter}
 * @see {@link ModelParameter}
 * 
 * @author Christian Vecchiola
 *
 */
public class Parameter implements Cloneable {
	
	/**
	 * Default name for the parameter, used by the default constructor.
	 */
	public static final String DEFAULT_NAME = "?";
	
	/**
	 * A {@link String} representing the name of the parameter. By default
	 * is assigned to the default name which is a simple question mark.
	 */
	@JsonProperty("name")
	protected String name;
	
	/**
	 * A {@link Object} representing the value that has been bound to the
	 * parameter.
	 */
	@JsonProperty("value")
	protected Object value;
	
	/**
	 * Initialises an instance of the {@link Parameter} class. This constructor
	 * sets the value of the name to the default name of the parameter and its
	 * value to {@literal null}.
	 */
	public Parameter() {
		this(Parameter.DEFAULT_NAME, null);
		
	}
	/**
	 * Initializes an instance of the {@link Parameter} class with the given
	 * name.
	 * 
	 * @param name	a {@link String} representing the name of the parameter it
	 * 				cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	public Parameter(String name) {
		this(name, null);
	}
	/**
	 * Initialises an instance of the {@link Parameter} class with the given
	 * values.
	 * 
	 * @param name	a {@link String} representing the name of the parameter it
	 * 				cannot be {@literal null} or an empty string.
	 * @param value	a {@link Object} representing the value bound to the 
	 * 				parameter.	
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	@JsonCreator
	public Parameter(@JsonProperty("name") String name, @JsonProperty("value") Object value) {
		
		this.setName(name);
		this.value = value;
	}

	/**
	 * Sets the name of the parameter.
	 * 
	 * @param name	a {@link String} representing the name of the parameter it
	 * 				cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or 
	 * 									an empty string.
	 */
	public void setName(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		this.name = name;
	}
	
	/**
	 * Gets the name of the parameter.
	 * 
	 * @return	a {@link String} representing the name of the parameter. It cannot
	 * 			be {@literal null} or an empty string. The default value is set to
	 * 			{@link Parameter#DEFAULT_NAME}.
	 */
	public String getName() {
		
		return this.name;
	}
	
	/**
	 * Gets the value of the parameter.
	 * 
	 * @return	a {@link Object} representing the value bound to the parameter. By
	 * 			default is {@literal null}.
	 */
	public Object getValue() {
		
		return this.value;
	}
	
	/**
	 * Sets the value for the parameter.
	 * 
	 * @param value	a {@link Object} instance representing the value of the parameter.
	 * 				It can be {@literal null}.
	 */
	public void setValue(Object value) {
		
		this.value = value;
	}
	
	/**
	 * Gets the hash code for the parameter. {@link Parameter} instances are matched
	 * by name. 
	 * 
	 * @return 	the value of the {@link String#hashCode()} method applied to the 
	 * 			{@link Parameter#getName()}.
	 */
	@Override
	public int hashCode() {
		
		return this.getName().hashCode();
	}
	
	/**
	 * Checks whether the given instance of {@link Parameter} is equal to the current
	 * instance of {@link Parameter}. The equality test is performed by checking the
	 * name property.
	 * 
	 * @return  {@literal true} if the two parameters have the same name, {@literal false}
	 * 			otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		
		boolean areTheSame = (this == other);
		
		if (areTheSame == false) {
		
			if ((other != null) && (other instanceof Parameter)) {
					
				Parameter pOther = (Parameter) other;
				String name = this.getName();
				
				areTheSame = name.equals(pOther.getName());
			}
		}
		
		return areTheSame;
	}
	
	/**
	 * Returns a {@link String} representation for the parameter.
	 * 
	 * @return a {@link String} in the following form: [name: <i>name</i>, value: <i>value</i>].
	 */
	@Override
	public String toString() {
		
		return "[name: " + this.getName() + ", value: " + this.getValue() + "]";
	}
	
	/**
	 * Clones the current instance. This method clones the properties
	 * such as <i>name</i> and <i>value</i> and creates a new instance 
	 * of {@link Parameter}. This is a <b>shallow copy</i> implementation.
	 * 
	 * @return  a {@link Parameter} instance whose properties should be 
	 * 			the same as the original ones.
	 */
	@Override
	public Parameter clone() {
		
		// this is a shallow copy of the value of the parameters. For
		// the use that we make of this class, this property is only
		// set to: 
		//
		// - int
		// - double
		// - String
		// - boolean
		//
		// this means that they are all immutable and therefore can be
		// copied as than is rather than cloned.
		//
		Parameter zombie = this.newInstance();
		
		zombie.setName(this.getName());
		zombie.setValue(this.getValue());
		
		return zombie;
		
	}
	/**
	 * Factory method that is used to abstract away the creation of instance
	 * of the type so that the clone method can effectively leverage the super
	 * class definition for cloning the super class portion of an object.
	 * 
	 * @return	a {@link Parameter} instance configured with {@link Parameter#DEFAULT_NAME}
	 * 			as name.
	 */
	protected Parameter newInstance() {
		
		return new Parameter();
	}
}
