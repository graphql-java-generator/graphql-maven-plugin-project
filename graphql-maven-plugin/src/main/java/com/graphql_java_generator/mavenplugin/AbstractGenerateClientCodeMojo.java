/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * This class is the super class of all Mojos that generates the client code, that is the {@link GenerateClientCodeMojo}
 * mojo. It contains all parameters that are common to these goals. The parameters common to all goal are inherited from
 * the {@link AbstractGenerateCodeMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each Mojo.
 * 
 * 
 * @author etienne-sf
 */
public abstract class AbstractGenerateClientCodeMojo extends AbstractGenerateCodeMojo {

	// There is currently no specific parameter for the client mode/

	/** The mode is forced to {@link PluginMode#client} */
	protected PluginMode mode = PluginMode.client;

	protected AbstractGenerateClientCodeMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
