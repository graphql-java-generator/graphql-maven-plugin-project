/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

/**
 * This class contains all parameters for the <I>generateClientCode</I> goal/task.
 * 
 * @author etienne-sf
 */
public interface GenerateClientCodeConfiguration extends GenerateCodeCommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_GENERATE_DEPRECATED_REQUEST_RESPONSE = "true";

	/**
	 * <P>
	 * <I>Since 1.7.1 version</I>
	 * </P>
	 * <P>
	 * Generates a XxxxResponse class for each query/mutation/subscription, and (if separateUtilityClasses is true) Xxxx
	 * classes in the util subpackage. This allows to keep compatibility with code Developed with the 1.x versions of
	 * the plugin.
	 * </P>
	 * <P>
	 * The best way to use the plugin is to directly use the Xxxx query/mutation/subscription classes, where Xxxx is the
	 * query/mutation/subscription name defined in the GraphQL schema.
	 * </P>
	 */
	public boolean isGenerateDeprecatedRequestResponse();

	/** Logs all the configuration parameters (only when in the debug level) */
	@Override
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("-- start configuration --");
			getLog().debug(
					"The graphql-java-generator Plugin Configuration for the generateClientCode goal/task is -->");
			getLog().debug("    generateDeprecatedRequestResponse: " + isGenerateDeprecatedRequestResponse());
			logGenerateCodeCommonConfiguration();
			getLog().debug("-- end configuration --");
		}
	}

}
