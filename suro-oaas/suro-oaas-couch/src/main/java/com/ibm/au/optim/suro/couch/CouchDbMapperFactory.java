package com.ibm.au.optim.suro.couch;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdObjectMapperFactory;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Class <b>CouchDbMapperFactory</b>. This class extends {@link StdObjectMapperFactory}
 * and adds the capability for deserializing references to {@link Strategy}, {@link Run}
 * and {@link RunDetails} to the correspoding concrete implementations.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbMapperFactory extends StdObjectMapperFactory {

	/**
	 * Creates an {@link ObjectMapper} instance that can be used to deserialize
	 * and serialize the documents that are retrieved from and stored to an 
	 * instance of <i>CouchDb</i>.
	 * 
	 * @param connector	a {@link CouchDbConnector} instance that is used to
	 * 					interact with the <i>CouchDb</i> database.
	 * 
	 * @return an instance of {@link ObjectMapper}.
	 */
	@Override
	public ObjectMapper createObjectMapper(CouchDbConnector connector) {
		
		ObjectMapper mapper = super.createObjectMapper(connector);
		
		// we create a module that adds the mapping to the concrete 
		// types.
		//
		
		SimpleModule module = new SimpleModule("SuroTypes");
		//module.addAbstractTypeMapping(Run.class, CouchDbRun.class);
		//module.addAbstractTypeMapping(OptimizationResult.class, CouchDbOptimizationResult.class);
		
		mapper.registerModule(module);
		
		return mapper;
		
	}
}
