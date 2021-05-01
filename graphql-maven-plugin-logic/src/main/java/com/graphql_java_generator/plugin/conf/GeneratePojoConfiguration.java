package com.graphql_java_generator.plugin.conf;

/**
 * This class contains all parameters for the <I>generatePOJO</I> goal/task.
 * 
 * @author etienne-sf
 */
public interface GeneratePojoConfiguration extends GraphQLConfiguration {

	/**
	 * The mode is of no interest, here.
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns
	 *         {@link PluginMode#client}
	 */
	@Override
	default public PluginMode getMode() {
		return PluginMode.client;
	}

	/**
	 * The utility classes are not generated for this goal/task
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isGenerateUtilityClasses() {
		return false;
	}

	/**
	 * There is no utility classes for this goal.
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isSeparateUtilityClasses() {
		return true;
	}

}
