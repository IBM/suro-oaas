/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>Objective</b>. This class models an optimisation objective. An 
 * objective is define by a <i>name</i>, an <i>identifier</i>, and a 
 * <i>description</i>. Objectives are those variables that are either minimised
 * or maximised within an optimisation problem and that concur to define the
 * optimisation function being solved by an optimisation model.
 * 
 * @author Christian Vecchiola
 *
 */
public class Objective implements Cloneable {

	/**
	 * A {@link String} representing the friendly name of the objective.
	 * It cannot be {@literal null} or an empty {@link String}.
	 */
	@JsonProperty("label")
	protected String label;
	
	/**
	 * A {@link String} representing a description of the objective. It
	 * can be {@literal null} or an empty {@link String}.
	 */
	@JsonProperty("description")
	protected String description;
	
	/**
	 * A {@link String} representing the identifier of the objective. This
	 * is used by the optimisation model to map parameters as weights for
	 * the objective.
	 * 
	 * @see ModelParameter#getObjective()
	 */
	@JsonProperty("name")
	protected String name;
	
	/**
	 * Initialises an objective with the given <i>name</i>. The constructor sets the value of 
	 * the <i>identifier</i> property to <i>name</i> and the <i>description</i> to {@literal null}.
	 * 
	 * @param name			a {@link String} representing the friendly name of the objective. It 
	 * 						cannot be {@literal null} or an empty {@link String}.
	 *
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or an empty {@link String}.
	 */
	public Objective(String name) {
		this(name, name, null);
	}
	
	/**
	 * Initialises an objective with the given <i>name</i>, <i>identifier</i> and <i>description</i>.
	 * 
	 * @param label			a {@link String} representing the friendly name of the objective. It cannot 
	 * 						be {@literal null} or an empty {@link String}.
	 * 
	 * @param name 			a {@link String} representing the identifier of the objective. This
	 * 						is used by the optimisation model to map parameters as weights for
	 *						the objective. It cannot be {@literal null} or an empty {@link String}.
	 *
	 * @param description	a {@link String} containing a description of the objective. It can be 
	 * 						{@literal null} or an empty {@link String}.
	 *
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> or <i>identifier</i> is {@literal null} or
	 * 									an empty {@link String}.
	 */
	@JsonCreator
	public Objective(@JsonProperty("name") String name, 
					 @JsonProperty("label") String label, 
					 @JsonProperty("description") String description) {

		this.setName(name);
		this.setLabel(label);
		this.setDescription(description);
	}

	/**
	 * Gets the friendly name of the objective. A friendly name is used to refer to the
	 * objective and can be used as a label for the objective. 
	 * 
	 * @return	a {@link String} containing the friendly name. It cannot be {@literal null}
	 * 			nor an empty string or a string composed by only spaces.
	 */
	public String getLabel() {
		
		return this.label;
	}

	/**
	 * Sets the friendly name of the objective. A friendly name is used to refer to the
	 * objective and can be used as a label for the objective. 
	 * 
	 * @return	label 	a {@link String} containing the friendly name. It cannot be {@literal null}
	 * 					nor an empty string or a string composed by only spaces.
	 * 
	 * @throws IllegalArgumentException if <i>name</i> is {@literal null}, an empty {@link String}
	 * 									or a string composed by spaces only.
	 */
	public void setLabel(String label) {
		
		if ((label == null) || (label.trim().isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'label' cannot be null, an empty string or a string composed by spaces.");
		}
		
		this.label = label.trim();
	}

	/**
	 * Gets the description of the objective. This is a short text fragment (a paragraph or two)
	 * that describe the quantity being optimised.
	 * 
	 * @return	a {@link String} containing the objective description. It can be {@literal null}
	 * 			or an empty {@link String}.
	 */
	public String getDescription() {
		
		return this.description;
	}

	/**
	 * Sets the description of the objective. This is a short text fragment (a paragraph or two)
	 * that describe the quantity being optimised.
	 * 
	 * @param description	{@link String} containing the objective description. It can be {@literal null}
	 * 						or an empty {@link String}.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}

	/**
	 * Sets the identifier of the objective. An identifier is a programmatic name that is used
	 * in the algorithm defining the optimisation model to refer to the objective. This value
	 * is also used to map a parameter of the model as a weigth used for the objective. 
	 * 
	 * 
	 * @return	name 	a {@link String} containing the identifier value. It cannot be {@literal null}
	 * 					nor an empty string or a string composed by only spaces.
	 * 
	 * @throws IllegalArgumentException if <i>name</i> is {@literal null}, an empty {@link String}
	 * 									or a string composed by spaces only.
	 */
	public void setName(String name) {
		
		if ((name == null) || (name.trim().isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null, an empty string or a string composed by spaces.");
		}
		
		this.name = name.trim();
	}

	/**
	 * Gets the identifier of the objective. An identifier is a programmatic name that is used
	 * in the algorithm defining the optimisation model to refer to the objective. This value
	 * is also used to map a parameter of the model as a weight used for the objective. 
	 * 
	 * @return	a {@link String} containing the identifier value. It cannot be {@literal null}
	 * 			nor an empty string or a string composed by only spaces.
	 */
	public String getName() {
		
		return this.name;

	}

	/**
	 * Gets the hash code for the objective. {@link Objective} instances are identified by their 
	 * corresponding identifiers that are meant to be unique. Therefore, these are used for the
	 * hash code.
	 * 
	 * @return 	an {@literal int} value representing the hash code of the <i>identifier</i>
	 * 			property, as returned by {@link String#hashCode()} applied to the reference
	 * 			returned by {@link Objective#getName()}.
	 */
	@Override
	public int hashCode() {
		
		return this.getName().hashCode();
	}
	
	/**
	 * {@link Objective} instances are identified by their corresponding identifiers that 
	 * are meant to be unique. Therefore, the equality check is performed against the value
	 * of the {@link Objective#getName()} property.
	 * 
	 * @param other 	a {@link Object} reference pointing to the instance to use for the
	 * 					equality test. This instance should not be {@literal null} and it
	 * 					should be of type {@link Objective}.
	 * 
	 * @return  {@literal true} if <i>other</i> is an {@link Objective} instance and its 
	 * 			<i>identifier</i> property is equal to the identifier of this instance.
	 */
	@Override
	public boolean equals(Object other) {
		
		if (other == null) {
			
			return false;
		}
		
		if (other instanceof Objective) {
			
			Objective obj = (Objective) other;
			
			String thisId = this.getName();
			String otherId = obj.getName();
			
			return thisId.equals(otherId);
		}
		
		return false;
	}
	/**
	 * Provides a {@link String} representation of the objective.
	 * 
	 * @return a {@link String} in the form [label: <i>label</i>, name: <i>name</i>].
	 */
	public String toString() {
		
		return "[label: " + this.getLabel() + ", name: " + this.getName() + "]";
	}
	
	/**
	 * This method clones an {@link Objective} instance. The method performs a shallow copy
	 * of the <i>name</i>, <i>label</i>, and <i>description</i> properties which all are
	 * immutable {@link String}.
	 * 
	 * @return an {@link Objective} instance whose values are copied from the current instance.
	 */
	public Objective clone() {
		
		Objective zombie = new Objective(this.getName(), this.getLabel(), this.getDescription());
		return zombie;
	}
}
