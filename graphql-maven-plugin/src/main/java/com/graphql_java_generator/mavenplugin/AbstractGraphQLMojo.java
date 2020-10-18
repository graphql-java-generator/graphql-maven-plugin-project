/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * This class is the class that contains all parameters that are specific to the {@link GraphQLMojo} mojo. The
 * parameters common to all goal are inherited from the {@link AbstractGenerateCodeMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each Mojo.
 * 
 * @author etienne-sf
 */
public abstract class AbstractGraphQLMojo extends AbstractGenerateServerCodeMojo {

	/**
	 * The generation mode: either <I>client</I> or <I>server</I>. Choose client to generate the code which can query a
	 * graphql server or server to generate a code for the server side.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.mode", defaultValue = GraphQLConfiguration.DEFAULT_MODE)
	PluginMode mode;

	protected AbstractGraphQLMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
