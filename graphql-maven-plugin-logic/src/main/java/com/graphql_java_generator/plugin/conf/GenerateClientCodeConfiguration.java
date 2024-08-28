/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all parameters for the <I>generateClientCode</I> goal/task.
 * 
 * @author etienne-sf
 */
public interface GenerateClientCodeConfiguration extends GenerateCodeCommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_GENERATE_DEPRECATED_REQUEST_RESPONSE = "false";

	/**
	 * <P>
	 * <I>(since 1.7.1 version) Default value is _true_ for 1.x version, and _false_ for version 2.0 and after.</I>
	 * </P>
	 * <P>
	 * If this parameter is set to true, the plugin generates a XxxxResponse class for each query/mutation/subscription,
	 * and (if separateUtilityClasses is true) Xxxx classes in the util subpackage. This allows to keep compatibility
	 * with code Developed with the 1.x versions of the plugin.
	 * </P>
	 * <P>
	 * The recommended way to use the plugin is to directly use the Xxxx query/mutation/subscription executor classes,
	 * where Xxxx is the query/mutation/subscription name defined in the GraphQL schema. To do this, set this parameter
	 * to _false_, and use the plugin as described in the
	 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_spring">wiki client
	 * page</a>.
	 * </P>
	 */
	public boolean isGenerateDeprecatedRequestResponse();

	/**
	 * {@inheritDoc}
	 * <P>
	 * In client mode, the <A HREF="https://github.com/FasterXML/jackson">Jackson</A> annotations are always generated
	 * </P>
	 */
	@Override
	default public boolean isGenerateJacksonAnnotations() {
		return true;
	}

	/** Logs all the configuration parameters (only when in the debug level) */
	@Override
	public default void logConfiguration() {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("-- start configuration --");
			logger.debug("The graphql-java-generator Plugin Configuration for the generateClientCode goal/task is -->");
			logger.debug("    generateDeprecatedRequestResponse: " + isGenerateDeprecatedRequestResponse());
			logger.debug("    generateJacksonAnnotations: " + isGenerateJacksonAnnotations());
			logGenerateCodeCommonConfiguration();
			logger.debug("-- end configuration --");
		}
	}

}
