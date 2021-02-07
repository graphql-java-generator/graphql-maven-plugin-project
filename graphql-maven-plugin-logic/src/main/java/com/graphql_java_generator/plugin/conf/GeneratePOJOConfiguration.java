package com.graphql_java_generator.plugin.conf;

/**
 * This class contains all parameters for the <I>generatePOJO</I> goal/task.
 * 
 * @author etienne-sf
 */
public interface GeneratePOJOConfiguration extends GenerateCodeCommonConfiguration {

	/**
	 * The mode is of no interest, here.
	 * 
	 * @return The {@link GeneratePOJOConfiguration} implementation of this method always returns
	 *         {@link PluginMode#client}
	 */
	@Override
	default public PluginMode getMode() {
		return PluginMode.client;
	}

	/**
	 * There is no runtime sources for this goal.
	 * 
	 * @return The {@link GeneratePOJOConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isCopyRuntimeSources() {
		return false;
	}

	/**
	 * There is no utility classes for this goal.
	 * 
	 * @return The {@link GeneratePOJOConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isSeparateUtilityClasses() {
		return false;
	}

}
