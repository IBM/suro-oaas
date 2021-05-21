package com.ibm.au.jaws.web.core.runtime.impl;

import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.RuntimeContext;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentFacade;
import com.ibm.au.jaws.web.core.runtime.impl.PropertiesParameterSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class <b>EnvironmentHelper</b>. This class provides a collection of utilities 
 * that come handy when there is the need to create and configure instances of
 * {@link Environment} for the purpose of testing components. It does provide
 * different options for creating environment instances.
 *
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class EnvironmentHelper {

    /**
     * Creates a mocked environment with the provided properties. If the properties passed are null or empty, no
     * properties (parameters) will exist in the resulting environment. Parameters cannot be modified AFTER the
     * environment has been initialised. Attributes can be modified freely.
     *
     * @param properties	a {@link Properties} object that will be used to configure the generated environment
     * 						instance. It can be {@literal null}.
     * 
     * @return 	a {@link Environment} implementation whose configuration parameters are fed from the properties
     * 			object passed as argument if any.
     */
    public static Environment mockEnvironment(Properties properties) {
        
    	return EnvironmentHelper.mockEnvironment(properties, true);
    }
    
    
    public static Environment mockEnvironment(Properties properties, boolean bind) {
    	
    	if (properties == null) {
            properties = new Properties();
        }
        PropertiesParameterSource source = new PropertiesParameterSource(properties);
        RuntimeContext runtime = new InMemoryRuntimeContext();
        EnvironmentFacade<PropertiesParameterSource, RuntimeContext> facade = new EnvironmentFacade<>(source, runtime);
        
        if (bind == true) {
        	
        	facade.bind();
        }
        
        return facade;
    }
    
    /**
     * Creates a mocked environment with the properties that are read from the given input stream. The 
     * stream cannot be {@literal null}, but needs to be mapped to an existing properties file. The method 
     * first creates an instance of the {@link Properties} object to load the collection of properties and 
     * then invokes {@link EnvironmentHelper#mockEnvironment(Properties)} to create the returned instance 
     * of {@link Environment}.
     * 
     * @param propStream	an {@link InputStream} implementation that provides access to the properties 
     * 						that will be used configure the environment. It cannot be {@literal null}.
     * 
     * @return	a {@link Environment} instance whose configuration parameters are fed from the properties
     * 			file accessible through <i>propStream</i>.
     * 
     * @throws IOException					if there is any error while reading from the given <i>propStream</i>.
     * @throws IllegalArgumentException		if <i>propStream</i> is {@literal null}.
     */
    public static Environment mockEnvironment(InputStream propStream) throws IOException {

    	return EnvironmentHelper.mockEnvironment(propStream, true);
    }
    
    
    public static Environment mockEnvironment(InputStream propStream, boolean bind) throws IOException{
    	
    	if (propStream == null) {
    		
    		throw new IllegalArgumentException("Parameter 'propStream' cannot be null.");
    	}
    	
    	Properties properties = new Properties();
    	properties.load(propStream);
    	
    	return EnvironmentHelper.mockEnvironment(properties, bind);
    }
}
