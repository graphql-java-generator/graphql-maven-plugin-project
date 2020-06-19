/**
 * 
 */
package com.graphql_java_generator.plugin;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class generates the relay schema, based on the given GraphQL schema(s). That is: it generates a GraphQL schema,
 * that is compliant with relay pagination, from the given GraphQL schema.<BR/>
 * The job is done by using this class as a Spring bean, and calling its {@link #generateRelaySchema()} method.
 * 
 * @author etienne-sf
 *
 */
// @Component
public class GenerateRelaySchema {

	@Autowired
	DocumentParser documentParser;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GenerateRelaySchemaConfiguration configuration;

	/** This method is the entry point, for the generation of the relay compatible schema */
	public void generateRelaySchema() {

	}
}
