/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 * {@inheritDoc}
	 * <P>
	 * In client mode, the <A HREF="https://github.com/FasterXML/jackson">Jackson</A> annotations are always generated.
	 * In server mode, these annotations are never generated.
	 * </P>
	 */
	@Override
	default public boolean isGenerateJacksonAnnotations() {
		return getMode().equals(PluginMode.client);
	}

	/**
	 * Logs all the configuration parameters for the <I>graphql</I> maven goal or <I>graphqlGenerateCode</I> gradle task
	 * (only when in the debug level)
	 */
	@Override
	public default void logConfiguration() {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("-- start configuration --");
			logger.debug("The graphql-java-generator Plugin Configuration for the generateClientCode goal/task is -->");
			logger.debug("    generateDeprecatedRequestResponse: " + isGenerateDeprecatedRequestResponse());
			logger.debug(
					"The graphql-java-generator Plugin Configuration for the graphql goal or the generateServerCode task is -->");
			logGenerateServerCodeConfiguration(); // There is no parameter specific to the client mode
			logger.debug("-- end configuration --");
		}
	}
}
