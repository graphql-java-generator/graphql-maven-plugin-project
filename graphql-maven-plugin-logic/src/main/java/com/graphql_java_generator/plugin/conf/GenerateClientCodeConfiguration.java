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

	// There is currently no parameter that is specific to the client mode

	/** Logs all the configuration parameters (only when in the debug level) */
	@Override
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("-- start configuration --");
			getLog().debug(
					"The graphql-java-generator Plugin Configuration for the generateClientCode goal/task is -->");
			logGenerateCodeCommonConfiguration();
			getLog().debug("-- end configuration --");
		}
	}

}
