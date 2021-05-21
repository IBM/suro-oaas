package com.ibm.au.optim.suro.core.composer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.core.composer.components.InputComponent;
import com.ibm.au.optim.suro.core.composer.components.HospitalInputComponent;
import com.ibm.au.optim.suro.core.composer.components.TemporalInputComponent;
import com.ibm.au.jaws.data.utils.ReflectionUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Coordinates the creation and running of InputComponents, and uses them to generate the input file required by the
 * cplex mod file.
 *
 * @author brendanhaesler
 */
public class RunInputComposer {

	/**
	 * Logger to log events that occur in this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RunInputComposer.class);

	/**
	 * List of components that make up the input file.
	 */
	private List<InputComponent> components;

	/**
	 * The environment containing the controllers required by components to generate their sections.
	 */
	private Environment environment;

	/**
	 * The name of the component specification file relative to the resources directory.
	 */
	private String componentSpecFilename;

	/**
	 * The ID of the hospital to get data from.
	 */
	private String hospitalId;

	/**
	 * The start time of the schedule to be generated.
	 */
	private long timeFrom;

	/**
	 * The end time of the schedule to be generated.
	 */
	private long timeTo;

	/**
	 * Constructs a default instance of the RunInputComposer class.
	 */
	public RunInputComposer() {
		this.components = new ArrayList<>();
	}

	/**
	 * Writes the input .dat file required by cplex using the provided writer.
	 * @param writer The writer to use to write the input file.
	 * @return A value indicating whether the operation was successful.
	 */
	public boolean compose(Writer writer) {
		// read the input spec from file
		List<InputComponentSpecification> specifications;

		try {
			specifications = readComponentSpecification();
		} catch (IOException e) {
			LOGGER.error("Error reading input specification file.", e);
			return false;
		}

		// create component writers
		createComponentWriters(specifications);

		// for each component
		for (InputComponent component : components) {
			// create the values to be written. TODO: Multi-thread this to group DB access costs
			component.constructValues(environment);
		}

		// write the sections
		try {
			writeSections(writer);
		} catch (IOException e) {
			LOGGER.error("Error writing to output file", e);
			return false;
		}

		return true;
	}

	/**
	 * Gets the environment.
	 * @return The environment.
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Sets the environment.
	 * @param environment The new environment.
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Gets the filename of the input specification file.
	 * @return A filename relative to the resources directory.
	 */
	public String getComponentSpecFilename() {
		return componentSpecFilename;
	}

	/**
	 * Sets the filename of the input specification file.
	 * @param componentSpecFilename The new filename, relative to the resources directory.
	 */
	public void setComponentSpecFilename(String componentSpecFilename) {
		this.componentSpecFilename = componentSpecFilename;
	}

	/**
	 * Gets the id of the hospital.
	 * @return The id of the hospital.
	 */
	public String getHospitalId() {
		return hospitalId;
	}

	/**
	 * Sets the id of the hospital.
	 * @param hospitalId The new hospital id.
	 */
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;

		for (InputComponent writer : components) {
			if (writer instanceof HospitalInputComponent) {
				((HospitalInputComponent) writer).setHospitalId(hospitalId);
			}
		}
	}

	/**
	 * Gets the start time of the schedule to be generated.
	 * @return The start time of the schedule to be generated.
	 */
	public long getTimeFrom() {
		return timeFrom;
	}

	/**
	 * Sets the start time of the schedule to be generated.
	 * @param timeFrom The new start time of the schedule to be generated.
	 */
	public void setTimeFrom(long timeFrom) {
		this.timeFrom = timeFrom;

		for (InputComponent writer : components) {
			if (writer instanceof TemporalInputComponent) {
				((TemporalInputComponent) writer).setTimeFrom(timeFrom);
			}
		}
	}

	/**
	 * Gets the end time of the schedule to be generated.
	 * @return The end time of the schedule to be generated.
	 */
	public long getTimeTo() {
		return timeTo;
	}

	/**
	 * Sets the end time of the schedule to be generated.
	 * @param timeTo The new end time of the schedule to be generated.
	 */
	public void setTimeTo(long timeTo) {
		this.timeTo = timeTo;

		for (InputComponent writer : components) {
			if (writer instanceof TemporalInputComponent) {
				((TemporalInputComponent) writer).setTimeTo(timeTo);
			}
		}
	}

	/**
	 * Reads the component specification .json file.
	 * @return A {@link List} of {@link InputComponentSpecification} objects.
	 * @throws IOException If the component specification file cannot be read.
	 */
	protected List<InputComponentSpecification> readComponentSpecification() throws IOException {
		InputStream specStream = this.getClass().getClassLoader().getResourceAsStream(componentSpecFilename);
		List<InputComponentSpecification> result = new ObjectMapper().readValue(specStream, new TypeReference<List<InputComponentSpecification>>() {	});
		specStream.close();
		return result;
	}

	/**
	 * Instantiates {@link InputComponent}s according to provided {@link InputComponentSpecification}s.
	 * @param specifications A {@link List} of {@link InputComponentSpecification} objects.
	 */
	protected void createComponentWriters(List<InputComponentSpecification> specifications) {
		for (InputComponentSpecification spec : specifications) {
			InputComponent component = ReflectionUtils.createInstance(spec.getComponentClassName());
			component.readSpecification(spec);

			if (component instanceof HospitalInputComponent) {
				((HospitalInputComponent) component).setHospitalId(hospitalId);
			}

			if (component instanceof TemporalInputComponent) {
				((TemporalInputComponent) component).setTimeFrom(timeFrom);
				((TemporalInputComponent) component).setTimeTo(timeTo);
			}

			components.add(component);
		}
	}

	/**
	 * Orders and writes the {@link InputSection}s of the InputComponents.
	 * @param writer The writer to use to write the sections with.
	 * @throws IOException If the sections cannot be written with the provided writer.
	 */
	protected void writeSections(Writer writer) throws IOException {
		// create a structure to hold the sections in order
		SortedMap<Integer, InputSection> sections = new TreeMap<>();

		// for each component
		for (InputComponent component : components) {
			// insert its sections into the map
			for (InputSection section : component.getSections()) {
					sections.put(section.getOrder(), section);
			}
		}

		// write the sections
		for (InputSection section : sections.values()) {
			writer.write(section.toString());
			writer.write("\n");
		}
	}
}
