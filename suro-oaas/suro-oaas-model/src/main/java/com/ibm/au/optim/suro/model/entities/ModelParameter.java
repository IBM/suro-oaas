/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Class <b>OptimizationModelParameter</b>. This class extends {@link Parameter}
 * and adds type information about the parameter. The value when set needs to be
 * compliant with the specified type.
 * </p>
 * <p>
 * An parameter for the optimization model is subject to the following constraints:
 * <ul>
 * <li>it cannot be {@literal null}.</li>
 * <li>it has a defined type that can only be one of those defined by the type
 * {@link ParameterType}</li>
 * <li>it can constrain the value to a given range.</li>
 * <li>it may act as weight for a specified objective.</li>
 * </ul>
 * </p>
 * 
 * @author Christian Vecchiola
 *
 */
public class ModelParameter extends Parameter {

	
	/**
	 * Enum <b>ParameterType</b>. It provides a set of the
	 * allowed parameter types. The type for the parameter
	 * can be only one the corresponding java types.
	 * 
	 * @author Christian Vecchiola
	 *
	 */
	public enum ParameterType {
		

		/**
		 * Indicates a {@link Boolean} parameter.
		 */
		BOOLEAN,
		/**
		 * Indicates an {@link Integer} parameter.
		 */
		INT,
		/**
		 * Indicates a {@link Double} parameter.
		 */
		DOUBLE,
		/**
		 * Indicates a {@link String} parameter.
		 */
		STRING
	}
	
	/**
	 * {@link Map} implementation that maps the {@link Class} instances describing a Java type
	 * to the corresponding value of the {@link ParameterType} enumeration to use.
	 */
	private static Map<Class<?>, ParameterType> typeMap = new HashMap<Class<?>, ParameterType>();
	
	/**
	 * Static initialization for the map that references the 
	 * corresponding allowed type for each of the value of the
	 * {@link ParameterType} enumeration.
	 */
	static {
		
		typeMap.put(Boolean.class, ParameterType.BOOLEAN);
		typeMap.put(Integer.class, ParameterType.INT);
		typeMap.put(Double.class, ParameterType.DOUBLE);
		typeMap.put(String.class, ParameterType.STRING);
	}
	
	/**
	 * A {@link ParameterType} value indicating the type of the parameter.
	 */
	@JsonProperty("type")
	protected ParameterType type;
	
	/**
	 * A {@link Object} array that represents the range of accepted values for the parameter. This
	 * array can either be {@literal null} or must be of length 2. In this case the first value should
	 * represent the lower bound of the range and the second value the upper value of the range. A
	 * {@literal null} value to one of the bound value indicates that the range is not closed.
	 */
	@JsonProperty("range")
	protected Object[] range;
	/**
	 * A {@link String} containing a short informative text about the parameter. This can be used
	 * within the user interfaces to refer to the parameter rather than using its name that is more
	 * appropriate to be used in the script of the optimisation model. It can be {@literal null} or
	 * an empty string.
	 */
	@JsonProperty("label")
	protected String label;
	/**
	 * A {@link String} containing the description of the function of the parameter. I can be {@literal 
	 * null} or an empty string.
	 */
	@JsonProperty("description")
	protected String description;
	
	/**
	 * A {@link String} representing the identifier of the {@link Objective} instance for which this
	 * {@link ModelParameter} instance is used as a configurable weight. This value can
	 * be {@literal null} if the parameter does not represent the weight for an objective.
	 */
	@JsonProperty("objective")
	protected String objective;
	
	/**
	 * Initialises an instance of {@link ModelParameter} with the given parameters. The
	 * constructor first sets the name by invoking {@link Parameter#Parameter(String)} it then invokes
	 * {@link ModelParameter#setType(ParameterType)} to impose the type constraints. If
	 * the operation is successful it then imposes the ranges constraints by calling the method {@link 
	 * ModelParameter#setRange(Object[])} and finally sets the valye by calling the method
	 * {@link ModelParameter#setValue(Object)}.
	 * 
	 * 
	 * @param name	a {@link String} representing the name of the parameter it cannot be {@literal null} 
	 * 				or an empty string.
	 * 
	 * @param value	a {@link Object} representing the value bound to the parameter. The value must be
	 * 			 	compliant with the restrictions imposed by the parameters <i>type</i> and <i>range</i>.	
	 * 
	 * @param type	a {@link ParameterType} value that defines the type of the parameter. This attribute
	 * 				is used for serialisation purposes and to make the entity agnostic with respect to the
	 * 				implementation language (i.e. Java). It cannot be {@literal null}.
	 * 
	 * @param range a {@link Object} array that defines the lower and upper bound for the range constraint
	 * 				imposed on the parameter. This array can either be {@literal null} or must be of length 
	 * 				2. In this case the first value should represent the lower bound of the range and the 
	 * 				second value the upper value of the range. A {@literal null} value to one of the bound 
	 * 				value indicates that the range is not closed.
	 * 
	 * @param objective	a {@link String} representing the identifier of the {@link Objective} instance for 
	 * 					which this {@link ModelParameter} instance is used as a configurable 
	 * 					weight. This value can be {@literal null} if the parameter does not represent the 
	 * 					weight for an objective.
	 * 
	 * @throws IllegalArgumentException	if one of the following occurs:
	 * 									<ul>
	 * 									<li><i>name</i> is {@literal null} or an empty string</li>
	 * 									<li><i>type</i> is {@literal null}</li>
	 * 									<li><i>range</i> is invalid or incompatible with <i>type</i></li>
	 * 									<li><i>value</i> is incompatible with the <i>type</i></li>
	 * 									<li><i>value</i> is incompatible with the <i>range</i></li>
	 * 									</ul>
	 */
	public ModelParameter(String name, Object value, ParameterType type,  Object[] range, String objective) {
		
		this(name, value, type, range, objective, null, null);
		
	}
	
	/**
	 * Initialises an instance of {@link ModelParameter} with the given parameters. The
	 * constructor first sets the name by invoking {@link Parameter#Parameter(String)} it then invokes
	 * {@link ModelParameter#setType(ParameterType)} to impose the type constraints. If
	 * the operation is successful it then imposes the ranges constraints by calling the method {@link 
	 * ModelParameter#setRange(Object[])} and finally sets the valye by calling the method
	 * {@link ModelParameter#setValue(Object)}.
	 * 
	 * 
	 * @param name	a {@link String} representing the name of the parameter it cannot be {@literal null} 
	 * 				or an empty string.
	 * 
	 * @param value	a {@link Object} representing the value bound to the parameter. The value must be
	 * 			 	compliant with the restrictions imposed by the parameters <i>type</i> and <i>range</i>.	
	 * 
	 * @param type	a {@link ParameterType} value that defines the type of the parameter. This attribute
	 * 				is used for serialisation purposes and to make the entity agnostic with respect to the
	 * 				implementation language (i.e. Java). It cannot be {@literal null}.
	 * 
	 * @param range a {@link Object} array that defines the lower and upper bound for the range constraint
	 * 				imposed on the parameter. This array can either be {@literal null} or must be of length 
	 * 				2. In this case the first value should represent the lower bound of the range and the 
	 * 				second value the upper value of the range. A {@literal null} value to one of the bound 
	 * 				value indicates that the range is not closed.
	 * 
	 * @param objective	a {@link String} representing the identifier of the {@link Objective} instance for 
	 * 					which this {@link ModelParameter} instance is used as a configurable 
	 * 					weight. This value can be {@literal null} if the parameter does not represent the 
	 * 					weight for an objective.
	 *
	 * @param label		a {@link String} containing a short informative text about the parameter. This can 
	 * 					be used within the user interfaces to refer to the parameter rather than using its 
	 * 					name that is more appropriate to be used in the script of the optimisation model. 
	 * 					It can be {@literal null} or an empty string.
	 * 
	 * @param description	a {@link String} containing the description of the function of the parameter. I 
	 * 						can be {@literal null} or an empty string.
	 * 
	 * 
	 * @throws IllegalArgumentException	if one of the following occurs:
	 * 									<ul>
	 * 									<li><i>name</i> is {@literal null} or an empty string</li>
	 * 									<li><i>type</i> is {@literal null}</li>
	 * 									<li><i>range</i> is invalid or incompatible with <i>type</i></li>
	 * 									<li><i>value</i> is incompatible with the <i>type</i></li>
	 * 									<li><i>value</i> is incompatible with the <i>range</i></li>
	 * 									</ul>
	 */
	@JsonCreator
	public ModelParameter(@JsonProperty("name") String name, 
						  @JsonProperty("value") Object value, 
						  @JsonProperty("type") ParameterType type, 
						  @JsonProperty("range") Object[] range,
						  @JsonProperty("objective") String objective,
						  @JsonProperty("label") String label,
						  @JsonProperty("description") String description) {
		
		super(name);
		
		this.setType(type);
		this.setRange(range);
		this.setValue(value);
		this.setObjective(objective);
		this.setLabel(label);
		this.setDescription(description);
	}
	
	/**
	 * Gets a short informative text about the parameter. When defined this can be used
	 * in place of {@link ModelParameter#getName()} to refer to the parameter in user
	 * interfaces. By default it is {@literal null}.
	 * 
	 * @return	a {@link String} containing the descriptive label of the parameter or 
	 * 			{@literal null}.
	 */
	public String getLabel() {
		
		return this.label;
	}

	/**
	 * Sets a short informative text about the parameter. When defined this can be used
	 * in place of {@link ModelParameter#getName()} to refer to the parameter in user
	 * interfaces. It can be {@literal null}.
	 * 
	 * @param label	a {@link String} containing the descriptive label of the parameter or 
	 * 				{@literal null}.
	 */
	public void setLabel(String label) {
		
		this.label = label;
	}
	
	/**
	 * Gets the description of the parameter. Differently from {@link ModelParameter#getLabel()},
	 * which is meant to provide a short text about the parameter, mostly to be used as a title, 
	 * this attribute provides a more detailed description of the parameter. It can be {@literal 
	 * null} and by default it is {@literal null}.
	 * 
	 * @return	a {@link String} representing the description of the parameter of {@literal null}.
	 */
	public String getDescription() {
		
		return this.description;
	}
	/**
	 * Gets the description of the parameter. Differently from {@link ModelParameter#getLabel()},
	 * which is meant to provide a short text about the parameter, mostly to be used as a title, 
	 * this attribute provides a more detailed description of the parameter. It can be {@literal 
	 * null} and by default it is {@literal null}.
	 * 
	 * @return	a {@link String} representing the description of the parameter of {@literal null}.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	} 
	
	/**
	 * Gets the identifier of the objective (if any) for which this parameter act as 
	 * a configurable weight. Not all the model parameters are weights for objectives.
	 * Therefore, this value can be {@literal null}.
	 * 
	 * @return	a {@link String} matching an objective identifier, as returned by the
	 * 			{@link Objective#getName()} or {@literal null}.
	 */
	public String getObjective() {
		
		return this.objective;
	}

	
	/**
	 * Sets the identifier of the objective (if any) for which this parameter act as 
	 * a configurable weight. Not all the model parameters are weights for objectives.
	 * Therefore, this value can be {@literal null}.
	 * 
	 * @param objective	a {@link String} matching an objective identifier, as returned 
	 * 					by the {@link Objective#getName()} or {@literal null}.
	 */
	public void setObjective(String objective) {
		
		this.objective = objective;
	}
	
	/**
	 * This method sets the type for the given parameter. If the current range values
	 * and value for the parameter are not compatible with the type passed as argument
	 * these will be set to {@literal null}, otherwise they will be converted to the
	 * type mapped by <i>type</i>
	 * 
	 * @param type	a {@link ParameterType} value that indicates the type of the parameter.
	 * 				This argument cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if <i>type</i> is set to {@literal null}.
	 */
	public void setType(ParameterType type) {
		

		Object v = this.getValue();
		
		if (type == null) {
			
			throw new IllegalArgumentException("Parameter 'type' cannot be null.");
		}
		
		Object[] r = this.getRange();
		
		boolean compatible = ModelParameter.isCompatibleWith(type, v);
		if (compatible == false) {
				
			r = null;
			v = null;
			
		} else {
			
			// ok, it is compatible, but we need to convert 
			// it to the desired type.
			//
			v = ModelParameter.convertTo(type, v);
		}
		
		// we set the type
		//
		this.type = type;

		// we need to re-assign these values to ensure that
		// they are of the desired type. Here we call super
		// because we already know that the value is in range
		// we have only cast the values to other types (int -> double).
		//
		super.setValue(v);
		
		// there is no super method for this.
		//
		this.setRange(r);

	}
	
	/**
	 * Gets the value (if any) set as upper bound for the range of allowed value of the
	 * parameter.
	 * 
	 * @return	the value of the upper bound, or {@literal null} if the set of values for
	 * 			the parameter is not "upper" limited. In that case the limit will be the
	 * 			the corresponding limit of the underlying type.
	 */
	@JsonIgnore
	public Object getUpperBound() {
		
		return this.range != null ? this.range[1] : null;
	}

	
	/**
	 * Gets the value (if any) set as lower bound for the range of allowed value of the
	 * parameter.
	 * 
	 * @return	the value of the lower bound, or {@literal null} if the set of values for
	 * 			the parameter is not "lower" limited. In that case the limit will be the
	 * 			the corresponding limit of the underlying type.
	 */
	@JsonIgnore
	public Object getLowerBound() {
		
		return this.range != null ? this.range[0] : null;
	}
	
	/**
	 * Sets the value for the parameter. This method performs that given value is compatible
	 * wit the current type of defined for the {@link ModelParameter} instance and
	 * that if there is any range defined for the parameter is within the specified range.
	 * 	
	 * @param value		an {@link Object} instance that represents the value of the parameter.
	 * 					It can be {@literal null} but if an only if there is no bound range
	 * 					for the parameter type (both lower and upper bounds are not {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if <i>value</i> is incompatible with the parameter type
	 * 									defined by {@literal OptimizationModelParameter#getType()}
	 * 									or is incompatible with the range defined by the property
	 * 									{@link ModelParameter#getRange()}.
	 */
	@Override
	public void setValue(Object value) {
			
		ParameterType t = this.getType();
		
		Object newValue = ModelParameter.convertTo(t, value);
		
		// ok now we need to check that the value is in range.
		boolean inRange = ModelParameter.isInRange(t, this.getRange(), newValue);
		
		if (inRange == false) {
			
			throw new IllegalArgumentException("The value for the parameter is not in the predefined range {value: " + newValue + ", range: [" + this.getLowerBound() + ", " + this.getUpperBound() + "]}.");
		}
		
		super.setValue(newValue);
		
	}
	/**
	 * Gets the range (if any) of values allowed for the parameter. A range is composed by
	 * an upper bound and a lower bound, which both can be {@literal null} if there are no
	 * limitations on the values accepted by the parameter. in that case the limitation will
	 * be imposed by the underlying type used to map the values. 
	 * 
	 * @return	an array of {@link  Object}. It can be {@literal null} but if it is not then
	 * 			it contains two elements: the first being the lower bound and the second the
	 * 			upper bound. Both values can be {@literal null}. If both of them are not 
	 * 			{@literal null} than the lower bound is equal or smaller of the upper bound.
	 */
	public Object[] getRange() {

		if (this.range != null) {
			
			return new Object[] { this.getLowerBound(), this.getUpperBound() };
		}
		
		return null;
	}
	
	/**
	 * Sets the range (if any) of values allowed for the parameter. A range is composed by
	 * an upper bound and a lower bound, which both can be {@literal null} if there are no
	 * limitations on the values accepted by the parameter. in that case the limitation will
	 * be imposed by the underlying type used to map the values. 
	 * 
	 * @param range	an array of {@link  Object}. It can be {@literal null} but if it is not then
	 * 				must contains two elements: the first being the lower bound and the second the
	 * 				upper bound. Both values can be {@literal null}. If both of them are not 
	 * 				{@literal null} than the lower bound is equal or smaller of the upper bound.
	 * 				Moreover, the values of the bounds must be compatible with the type of the
	 * 				parameter and the current value set for the parameter.
	 * 
	 * @throws IllegalArgumentException	if <i>range</i> is not {@literal null} and any of the
	 * 									following conditions apply:
	 * 									<ul>
	 * 									<li>type incompatibility of the bounds with the set value
	 * 									of the parameter</li>
	 * 									<li>invalid structure of the range (more than two elements
	 * 									or less than two)</li>
	 * 									<li>inconsistency of the bound values (upper < lower)</li>
	 * 									<li>inconsistency with the set value for the parameter</li>
	 * 									</ul>
	 */
	public void setRange(Object[] range) { 
		
		Object[] r = ModelParameter.checkRange(this.getType(), range);
		
		if (r != null) {
			
			boolean allowed = ModelParameter.isInRange(this.getType(), r, this.getValue());
			if (allowed == false) {
				
				throw new IllegalArgumentException("The parameter 'range' is not compatible with the current value of the parameter");
			}
		}
		
		this.range = r;
	}
	
	/**
	 * Gets the type set for the parameter. The type for instances of {@link 
	 * OptmizationModelParameter} is limited to any of the following: <i>INT</i>,
	 * <i>DOUBLE</i>, <i>BOOLEAN</i>, and <i>STRING</i>.
	 * 
	 * @return	a value of the {@link ParameterType} enumeration.
	 */
	public ParameterType getType() {
		
		return this.type;
	}
	
	/**
	 * Provides a string representation for instances of the {@link ModelParameter}
	 * class.
	 * 
	 * @return a {@link String} in the following form: [name: <i>name</i>, value: <i>value</i>, type: <i>type</i>, range: <i>range</i>].
	 * 
	 */
	@Override
	public String toString() {
		
		Object[] r = this.getRange();
		
		return "[name: " + this.getName() + ", value: " + this.getValue() + ", type: " + this.getType() + ", range: " + (r == null ? null : "{" + r[0] + ", " + r[1] + "}") + "]";
		
	}
	
	/**
	 * <p>
	 * This method clones an instance of {@link ModelParameter}. The method 
	 * uses the super-class implementation of the method to clone the portion 
	 * of {@link ModelParameter} that inherits from {@link Parameter}, then
	 * it completes the process with the properties specifically defined by the
	 * {@link ModelParameter} class.
	 * </p>
	 * <p>
	 * The properties <i>value</i>, and the lower and upper bounds of the range
	 * are shallow copied.
	 * </p>
	 * 
	 * @return 	an instance of {@link TemplateParameter} whose property are either
	 * 			copied or cloned from the current instance.
	 */
	@Override
	public Parameter clone() {
		
		ModelParameter zombie = (ModelParameter) super.clone();
		
		// we do not need to copy neither the name nor the value
		// or the type. We only need to fixup the range and the
		// objective and this should not be a problem because we
		// should have compatibility.
		
		zombie.setObjective(this.getObjective());
		
		// we replicate the container (array) for the range but
		// the bounds ar shallow copied.
		//
		Object[] rng = null;
		if (this.getRange() != null) {
			rng = new Object[ ] { this.getLowerBound(), this.getUpperBound() };
		}
		zombie.setRange(rng);
		
		
		zombie.setLabel(this.getLabel());
		zombie.setDescription(this.getDescription());
		
		return zombie;
	}
	
	/**
	 * Creates an instance of {@link ModelParameter}. This method overrides
	 * the base-class version to change the type of the instance returned to
	 * {@link ModelParameter}.
	 * 
	 * @return 	an instance of {@link ModelParameter} whose <i>name</i> and
	 * 			<i>type</i> have been set up with the corresponding values
	 * 			in this instance.	
	 */
	@Override
	protected Parameter newInstance() {
		
		return new ModelParameter(this.getName(), null, this.getType(), null, null);
	}
	
	// ================================================= 
	//
	//          SUPPORT METHODS FOR VALIDATION
	//
	// =================================================
	
	/**
	 * This method checks that the given <i>range</i> is compatible with the given <i>type</i>
	 * and also that it is structurally valid to describe a possible range of values for the
	 * underlying Java type mapped by <i>type</i>.
	 * 
	 * @param type		a {@link ParameterType} value indicating the type of the element of the
	 * 					range. It cannot be {@literal null}.
	 * @param range		a {@link Object} array. This can be {@literal null} but if it is not it
	 * 					must of be compose by two elements, which have a compatible type with
	 * 					<i>type</i> and must be ordered as [<i>lower</i>, <i>upper</i>].
	 * 
	 * @return	if <i>range</i> is validated, a corresponding {@link Object} array whose not 
	 * 			{@link null} bounds have been converted to corresponding instances of the type
	 * 			mapped by <i>type</i>. If <i>range</i> is {@literal null}, {@literal null} is
	 * 			returned.
	 * 
	 * 
	 * @throws IllegalArgumentException	if <i>range</i> is not of compatible type or a valid range.
	 */
	private static Object[] checkRange(ParameterType type, Object[] range) {
		
		Object[] rng = null;
		
		if (range != null) {
			
			if (range.length != 2) {
				
				throw new IllegalArgumentException("The parameter 'range' does not constitute a valid range (length must be equal to 2).");
			}
			
			rng = new Object[2];
			
			// ok now we check that there is a compatible value
			// and that the range is properly set up.
			
			try {
				
				
				rng[0] = ModelParameter.convertTo(type, range[0]);
				rng[1] = ModelParameter.convertTo(type, range[1]);

				
			} catch(IllegalArgumentException ilex) {
				
				throw new IllegalArgumentException("The parameter 'range' does not contain bounds with compatible with the type {type: " + type + ", range: [" + range[0] + ", " + range[1] + "]}."  );
			}
			
			if ((rng[0] != null) && (rng[1] != null)) {
			
				
				int comparison = ModelParameter.compareTo(type, rng[0], rng[1]);
				if (comparison > 0) {
					
					throw new IllegalArgumentException("The parameter 'range' has invalid bounds [" + rng[0] + ", " + rng[1] +"].");
				}
			
			}
			
		} 
		
		return rng;
	}
	
	/**
	 * This method checks that the given <i>value</i> is within the defined <i>range</i>.
	 * The method uses the information provided by <i>type</i> to perform the appropriate
	 * comparisons between the given value and the bounds of the range when they are not
	 * {@literal null}.
	 * 
	 * @param type	a {@link ParameterType} value indicating the underlying type of the
	 * 				values being compared.
	 * @param range	a {@link Object} array that must be either {@literal null} or composed 
	 * 				by two elements indicating the lower and upper bounds of the range,
	 * 				respectively. Any of these two can be {@literal null}, in that case
	 * 				the corresponding bound is unlimited. It is expected that non {@literal
	 * 				null} values are of the type identified by <i>type</i> and properly
	 * 				ordered in the array.
	 * @param value	a {@link Object} representing the value to test for range inclusion.
	 * 				It is allowed that <i>value</i> is {@literal null}. In that case no
	 * 				range check will be applied and the method will return {@literal true}.
	 * 
	 * 
	 * @return	{@literal true} if <i>range</i> is {@literal null} or the value is within
	 * 			the identified range, or <i>value</i> is {@literal null}. {@literal false} 
	 * 			otherwise.
	 * 
	 * @throws IllegalArgumentException if there is some type incompatibility between 
	 * 									the specified type and the values.
	 */
	private static boolean isInRange(ParameterType type, Object[] range, Object value) {

		
		// if there is no range or the value is null, the value cannot be tested for
		// range inclusion and therefore the method returns true.
		//
		if ((range == null) || (value == null)) {
			
			return true;
		}
		
		// if at least one of the two bounds is null and the value
		// is null, we still pass the test.
		//
		if ((range[0] == null) && (range[1] == null)) {
			
			return true;
		}
		
		// if we get here, this means that the value is not null or at
		// least one of the two bounds is not null.
		int comparison = 0;
		
		// the lower bound if not null needs to be smaller or equal
		// to the value.
		//
		Object bound = range[0];
		if (bound != null) {
			
			comparison = ModelParameter.compareTo(type, bound, value);
			if (comparison > 0) {
				
				return false;
			}
		}
		
		// the upper bound if not null needs to be bigger or equal
		// to the value.
		//
		bound = range[1];
		if (bound != null) {
			comparison = ModelParameter.compareTo(type, bound, value);
			if (comparison < 0) {
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method performs a comparison between <i>first</i> and <i>second</i>. The method is
	 * essentially a dispatcher to the {@link Comparable#compareTo(Object)} that is applicable
	 * for the type mapped by <i>type</i>. Once the type is identified both <i>first</i> and
	 * <i>second</i> are cast to a reference of the corresponding underlying Java type and then
	 * the method {@link Comparable#compareTo(Object)} is invoked on <i>first</i> by passing
	 * <i>second</i> as an argument. 
	 * 
	 * @param type		a {@link ParameterType} value that is used to identify the underlying 
	 * 					Java type of <i>first</i> and <i>second</i>.
	 * 
	 * @param first		a {@link Object} reference representing the first candidate of the 
	 * 					comparison.
	 * 
	 * @param second 	a {@link Object} reference representing the second candidate of the
	 * 					comparison.
	 * 
	 * @return	the return value of {@link Comparable#compareTo(Object)} when applied to 
	 * 			<i>first</i> with <i>second</i> as argument.
	 */
	private static int compareTo(ParameterType type, Object first, Object second) {
		
		int comparison = 0;
		
		switch(type) {
			
			case INT: 		comparison = ((Integer) first).compareTo((Integer) second); break;
			case DOUBLE:	comparison = ((Double) first).compareTo((Double) second); 	break;
			case BOOLEAN: 	comparison = ((Boolean) first).compareTo((Boolean) second); break;
			case STRING: 	comparison = ((String) first).compareTo((String) second); 	break;		
		
		}
		
		return comparison;
	}
	
	/**
	 * This method checks whether the given value is type compatible with the specified type.
	 * The method first checks that <i>value</i> is not {@literal null} and if not it retrieves
	 * the underlying {@link Class} instance defining the type of the value and checks whether
	 * it is the same mapped by <i>type</i>. If the two types are different the method checks
	 * whether value is of type {@link Integer} and the current type is {@link Double}, which
	 * is the only case in which it is possible to make a safe conversion of type.
	 * 
	 * @param type	a {@link ParameterType} value that defines the type of the parameter and
	 * 				maps the underlying Java type used to perform type checks.
	 * 
	 * @param value	a {@link Object} representing the candidate value. It can be {@literal null}.
	 * 
	 * 
	 * @return	{@literal true} if <i>value</i> is {@literal null} or type compatible with <i>type</i>.
	 * 			{@literal false} otherwise.
	 */
	private static boolean isCompatibleWith(ParameterType type, Object value) {

		// we make all the types nullable therefore, null is always compatible.
		// with any type.
		
		if (value == null)  {
			
			return true;
		}
		
		ParameterType updateType = typeMap.get(value.getClass());

		return (updateType == type) || ((type == ParameterType.DOUBLE) && (updateType == ParameterType.INT));
	}
	
	/**
	 * This method it used to convert <i>value</i> to an instance of the underlying Java type
	 * mapped by <i>type</i>. The method first invokes {@link ModelParameter#isCompatibleWith(ParameterType, Object)}
	 * by passing <i>type</i> and <i>value</i>. If the test is positive the conversion to is
	 * performed. Because the only type conversion supported is from {@link Integer} to {@link
	 * Double}, this will be the only case in which a new instance is created, in all the other
	 * cases that the method returns a positive outcome, the original reference is returned and
	 * this means that no conversion is needed.

	 * @param type	a {@link ParameterType} value that defines the type of the parameter and
	 * 				maps the underlying Java type used to perform type compatibility check.
	 * 
	 * @param value	a {@link Object} representing the candidate value. It cannot be {@literal null}.
	 * 
	 * @return	{@literal true} if the underlying Java type mapped by <i>type</i> is the same as
	 * 			the type of <i>value</i>, or <i>type</i> is equal to {@link ParameterType#DOUBLE}
	 * 			and value is of type {@link Integer}.
	 */
	private static Object convertTo(ParameterType type, Object value) {
		
		boolean compatible = ModelParameter.isCompatibleWith(type, value);
		
		if (compatible == false) {
			
			throw new IllegalArgumentException("Value " + value + " is not compatible with " + type);
		}
		
		// this is the only case in which we need to convert.
		//
		if ((value instanceof Integer) && (type == ParameterType.DOUBLE)) {
			
			value = new Double((Integer) value);
		}
		
		return value;
		
	}
	

}
