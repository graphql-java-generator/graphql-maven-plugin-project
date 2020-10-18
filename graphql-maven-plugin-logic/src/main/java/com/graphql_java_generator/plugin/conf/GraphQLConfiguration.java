/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This interface contains all the configuration parameters for the <I>graphql</I> goal (Maven) or task (Gradle) of the
 * plugin, as an interface.<BR/>
 * All these methods are directly the property names, to map against a Spring {@link Configuration} that defines the
 * {@link Bean}s. These beans can then be reused in Spring Component, thank to Spring IoC and its dependency injection
 * capability.
 * 
 * @author etienne-sf
 */
public interface GraphQLConfiguration extends GenerateClientCodeConfiguration, GenerateServerCodeConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_MODE = "client";

	/**
	 * Logs all the configuration parameters for the <I>graphql</I> maven goal or <I>graphqlGenerateCode</I> gradle task
	 * (only when in the debug level)
	 */
	@Override
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("-- start configuration --");
			getLog().debug(
					"The graphql-java-generator Plugin Configuration for the graphql goal or the graphqlGenerateCode task is -->");
			logGenerateServerCodeConfiguration(); // There is no parameter specific to the client mode
			getLog().debug("-- end configuration --");
		}
	}
}
