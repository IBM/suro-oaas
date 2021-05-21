package com.ibm.au.optim.suro.model.entities.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enumeration lists all the possible mapping types that can be specified in an {@link OutputMapping}.
 *
 * @author Peter Ilfrich
 */
public enum MappingType {

    /**
     * Standard mapping type used for most mappings (-> ComplexResultMapper)
     */
    COMPLEX ("complex"),

    /**
     * Mapping type used to horizontally append multiple "complex" elements (-> ComplexAppendResultMapper)
     */
    COMPLEX_APPEND ("complex-append"),

    /**
     * Mapping type used to transform from an array of structured JSON objects to one single JSON object with field
     * names representing values of specified fields within the array entries (-> JsonCategoryResultMapper)
     */
    JSON_CATEGORY ("json-category"),

    /**
     * Mapping type to convert JSON field names of entries into columns in a CSV (-> KeyToColumnResultMapper)
     */
    KEY_TO_COLUMN ("key-to-column"),

    /**
     * Custom mapping type specifying a transformer class that is used to perform the transformation.
     */
    TRANSFORMER ("transformer");


    /**
     * The type identifier
     */
    private final String type;


    /**
     * Creates a new mapping type.
     * @param type - the identifier of the type
     */
    MappingType(String type) {
        this.type = type;
    }


    /**
     * Retrieves the enumeration entry specified by the identifier.
     * @param typeIdentifier - the technical identifier of the type.
     * @return - the enumeration entry or null if the identifier cannot be found.
     */
    @JsonCreator
    public static MappingType getType(String typeIdentifier) {
        MappingType[] types = new MappingType[] { COMPLEX, COMPLEX_APPEND, JSON_CATEGORY, KEY_TO_COLUMN, TRANSFORMER };
        for (MappingType type : types) {
            if (type.type.equals(typeIdentifier)) {
                return type;
            }
        }
        return null;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.type;
    }
}
