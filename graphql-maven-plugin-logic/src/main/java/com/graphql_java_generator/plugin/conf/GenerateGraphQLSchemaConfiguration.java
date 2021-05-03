package com.graphql_java_generator.plugin.conf;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interface contains the getters for all the plugin parameters for the <I>generate-relay-schema</I> goal (Maven)
 * or task (Gradle) of the plugin.
 * 
 * @author etienne-sf
 *
 */
public interface GenerateGraphQLSchemaConfiguration extends CommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"

	public final String DEFAULT_TARGET_SCHEMA_FILE_NAME = "generated_schema.graphqls";
	public final String DEFAULT_RESOURCE_ENCODING = "UTF-8";
	public final String DEFAULT_TARGET_FOLDER = "/generated-resources/graphql-maven-plugin_generate-relay-schema";

	/** The encoding for the generated resource files */
	public String getResourceEncoding();

	/** The folder where the generated GraphQL schema will be stored */
	public File getTargetFolder();

	/**
	 * The name of the target filename, in which the schema is generated. This file is stored in the folder, defined in
	 * the <I>targetFolder</I> plugin parameter.
	 */
	public String getTargetSchemaFileName();

	/** Logs all the configuration parameters, in the debug level */
	@Override
	public default void logConfiguration() {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("The graphql-java-generator Plugin Configuration for the 'merge' goal is -->");
			logger.debug("  resourceEncoding: " + getResourceEncoding());
			logger.debug("  targetFolder: " + getTargetFolder());
			logger.debug("  targetSchemaFileName: " + getTargetSchemaFileName());
			logger.debug("  COMMON PARAMETERS:");
			logger.debug("    schemaFileFolder: " + getSchemaFileFolder());
			logger.debug("    schemaFilePattern: " + getSchemaFilePattern());
			logger.debug("    Templates: "
					+ (Objects.nonNull(getTemplates())
							? getTemplates().entrySet().stream()
									.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
									.collect(Collectors.joining(", "))
							: StringUtils.EMPTY));
			logger.debug("-- end configuration --");
		}
	}
}
