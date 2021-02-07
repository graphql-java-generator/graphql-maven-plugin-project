package com.graphql_java_generator.plugin;

/**
 * This class is responsible for the execution of the goal/task. <BR/>
 * There should be one such component in the relevant package, for each goal.
 * 
 * @author etienne-sf
 */
public interface PluginExecutor {

	/**
	 * Actual execution of the goal/task. <BR/>
	 * This method is the method called by the Maven and Gradle plugins. There should be one such component in the
	 * relevant package, for each goal.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception;

}
