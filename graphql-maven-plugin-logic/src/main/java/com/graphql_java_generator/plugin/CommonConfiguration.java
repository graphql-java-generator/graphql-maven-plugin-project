/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;

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
public interface CommonConfiguration {
	To be continued...

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"

	public final String DEFAULT_SCHEMA_FILE_FOLDER = "/src/main/resources";
	public final String DEFAULT_SCHEMA_FILE_PATTERN = "*.graphqls";

	/**
	 * The logging system to use. It's implemented against the JDK one, to avoid useless dependencies. For instance you
	 * can use log4j2, by adding the 'Log4j JDK Logging Adapter' (JUL)
	 */
	public Logger getLog();

	/**
	 * The main resources folder, typically '/src/main/resources' of the current project. That's where the GraphQL
	 * schema(s) are expected to be: in this folder, or one of these subfolders
	 */
	public File getSchemaFileFolder();

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 */
	public String getSchemaFilePattern();

	/** Logs all the configuration parameters, in the debug level */
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("  COMMON PARAMETERS: ");
			getLog().debug("  schemaFileFolder: " + getSchemaFileFolder());
			getLog().debug("  schemaFilePattern: " + getSchemaFilePattern());
		}
	}

}
