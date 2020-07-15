package com.graphql_java_generator.plugin;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * This interface contains the getters for all the plugin parameters for the <I>generate-relay-schema</I> goal (Maven)
 * or task (Gradle) of the plugin.
 * 
 * @author etienne-sf
 *
 */
public interface MergeConfiguration extends CommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"

	public final String DEFAULT_SCHEMA_FILE_NAME = "schema.graphqls";
	public final String DEFAULT_RESOURCE_ENCODING = "UTF-8";
	public final String DEFAULT_TARGET_FOLDER = "/generated-resources/graphql-maven-plugin_generate-relay-schema";

	/**
	 * The name of the target filename, in which the schema is generated. This file is stored in the folder, defined in
	 * the <I>schemaFileFolder</I> plugin parameter.
	 */
	public String getSchemaFileName();

	/** The encoding for the generated resource files */
	public String getResourceEncoding();

	/** The folder where the generated GraphQL schema will be stored */
	public File getTargetFolder();

	/** Logs all the configuration parameters, in the debug level */
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("The graphql-java-generator Plugin Configuration for the generate-relay-schema is:");
			getLog().debug("  schemaFileName: " + getSchemaFileName());
			getLog().debug("  resourceEncoding: " + getResourceEncoding());
			getLog().debug("  targetFolder: " + getTargetFolder());
			getLog().debug("  COMMON PARAMETERS:");
			getLog().debug("    schemaFileFolder: " + getSchemaFileFolder());
			getLog().debug("    schemaFilePattern: " + getSchemaFilePattern());
			getLog().debug("    Templates: "
					+ (Objects.nonNull(getTemplates())
							? getTemplates().entrySet().stream()
									.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
									.collect(Collectors.joining(", "))
							: StringUtils.EMPTY));
		}
	}
}
