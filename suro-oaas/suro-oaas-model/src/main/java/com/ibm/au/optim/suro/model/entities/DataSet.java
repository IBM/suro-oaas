package com.ibm.au.optim.suro.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.InputStream;

/**
 * A data set represents a set of data points and prediction parameters, which can be combined into a .dat file which
 * is used for the optimisation. The dat file can either be provided directly (hard-coded) or computed (with hard-coded,
 * calculated and/or predicted tupel values)
 *
 * @author Peter Ilfrich
 */
public class DataSet extends Entity {


    /**
     * The name of the dat file as it is stored as attachment in the repository
     */
    @JsonIgnore
    public static final String FILENAME_DAT_FILE = "data.dat";

    @JsonProperty("label")
    private String label;

    @JsonProperty("modelId")
    private String modelId;

    @JsonIgnore
    private InputStream datFile;

    @Deprecated
    @JsonProperty
    private int datFileVersion;

    /**
     * The default constructor used for serialisation / deserialisation
     */
    public DataSet() {

    }

    /**
     * Creates a new data set with the specified data.
     * @param label - the label of the set
     * @param modelId - the model id the set belongs to
     * @param datFile - the content of the dat file represented as inputstream
     */
    public DataSet(String label, String modelId, InputStream datFile) {
        this.label = label;
        this.modelId = modelId;
        this.datFile = datFile;
    }

    /**
     * Provides the label / name of this data set
     * @return - the name/label of this data set
     */
    public String getLabel() {
        return label;
    }


    /**
     * Returns the content of the dat file as an input stream.
     * @return
     */
    public InputStream getDatFile() {
        return datFile;
    }


    /**
     * Getter for the model id
     * @return - the id of the model this data set belongs to
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Sets the content of the dat file (provided as input stream).
     * @param datFile
     */
    public void setDatFile(InputStream datFile) {
        this.datFile = datFile;
    }

    /**
     * Setter for the model id
     * @param modelId - the new model id used for this data set
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    /**
     * Setter for the label of the data set
     * @param newLabel - the new label of the set
     */
    public void setLabel(String newLabel) {
        this.label = newLabel;
    }


    /**
     * Retrieves the current version of the dat file.
     * @deprecated
     * @return - the version of the dat file.
     */
    @Deprecated
    public int getDatFileVersion() {
        return datFileVersion;
    }

    /**
     * Sets a new version for the dat file. This is used for data migration of existing data sets
     * @deprecated
     * @param datFileVersion
     */
    @Deprecated
    public void setDatFileVersion(int datFileVersion) {
        this.datFileVersion = datFileVersion;
    }
    
	/**
	 * <p>
	 * This method clones a {@link DataSet}. The {@link String} properties
	 * of a {@link DataSet} are not cloned because the underlying type is
	 * immutable. The method first, invokes the super-class version of the
	 * method and then clones or copies the properties directly defined by
	 * the {@link DataSet} class.
	 * </p>
	 * 
	 * @return  a {@link DataSet} instance that represents the clone of the
	 * 			the current instance.
	 */
	@Override
	public Entity clone() {
		
		// this call to the super-class version of the method
		// does take care of the super-class defined properties.
		//
		DataSet zombie = (DataSet) super.clone();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		
		zombie.setLabel(this.getLabel());
		zombie.setModelId(this.getModelId());
		zombie.setDatFileVersion(this.getDatFileVersion());
		zombie.setDatFile(this.getDatFile());
		//
		
		return zombie;
	}
	
	/**
	 * This method creates an instance of {@link DataSet}. The method overrides 
	 * the base class implementation to change the type of the instance returned 
	 * by the method to {@link DataSet}.
	 * 
	 * @return an instance of {@link DataSet}.
	 */
	@Override
	protected Entity newInstance() {
		
		return new DataSet();
	}
}
