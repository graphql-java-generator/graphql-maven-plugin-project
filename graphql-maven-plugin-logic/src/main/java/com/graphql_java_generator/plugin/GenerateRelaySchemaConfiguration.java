package com.graphql_java_generator.plugin;

import java.io.File;

/**
 * This interface contains the getters for all the plugin parameters for the <I>generate-relay-schema</I> goal (Maven)
 * or task (Gradle) of the plugin.
 * 
 * @author etienne-sf
 *
 */
public interface GenerateRelaySchemaConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"

	public final String DEFAULT_SCHEMA_FILE_FOLDER = "/src/main/resources";
	public final String DEFAULT_SCHEMA_FILE_NAME = "relay.graphqls";
	public final String DEFAULT_SCHEMA_FILE_PATTERN = "*.graphqls";
	public final String DEFAULT_RESOURCE_ENCODING = "UTF-8";
	public final String DEFAULT_TARGET_FOLDER = "/generated-resources/graphql-maven-plugin_generate-relay-schema";

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
	 * The name of the target filename, in which the schema is generated. This file is stored in the folder, defined in
	 * the <I>schemaFileFolder</I> plugin parameter.
	 */
	public String getSchemaFileName();

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 */
	public String getSchemaFilePattern();

	/** The encoding for the generated resource files */
	public String getResourceEncoding();

	/** The folder where the generated GraphQL schema will be stored */
	public File getTargetFolder();

	/** Logs all the configuration parameters, in the debug level */
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("The graphql-java-generator Plugin Configuration for the generate-relay-schema is:");
			getLog().debug("  schemaFileFolder: " + getSchemaFileFolder());
			getLog().debug("  schemaFilePattern: " + getSchemaFilePattern());
			getLog().debug("  resourceEncoding: " + getResourceEncoding());
			getLog().debug("  targetFolder: " + getTargetFolder());
		}
	}
}
