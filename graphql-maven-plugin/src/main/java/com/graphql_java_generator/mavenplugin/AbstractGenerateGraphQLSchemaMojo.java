/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.conf.Logger;

/**
 * @author etienne-sf
 */
public abstract class AbstractGenerateGraphQLSchemaMojo extends AbstractCommonMojo
		implements GenerateGraphQLSchemaConfiguration {

	private MavenLogger log = null;

	/** The encoding for the generated resource files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.resourceEncoding", defaultValue = GenerateGraphQLSchemaConfiguration.DEFAULT_RESOURCE_ENCODING)
	String resourceEncoding;

	/** The folder where the generated GraphQL schema will be stored */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetFolder", defaultValue = GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_FOLDER)
	File targetFolder;

	/**
	 * The name of the target filename, in which the schema is generated. This file is stored in the folder, defined in
	 * the <I>targetFolder</I> plugin parameter.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSchemaFileName", defaultValue = GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME)
	String targetSchemaFileName;

	AbstractGenerateGraphQLSchemaMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}

	@Override
	public Logger getPluginLogger() {
		if (log == null) {
			log = new MavenLogger(this);
		}
		return log;
	}

	@Override
	public File getSchemaFileFolder() {
		return schemaFileFolder;
	}

	@Override
	public String getSchemaFilePattern() {
		return schemaFilePattern;
	}

	@Override
	public String getResourceEncoding() {
		return resourceEncoding;
	}

	@Override
	public File getTargetFolder() {
		return targetFolder;
	}

	@Override
	public String getTargetSchemaFileName() {
		return targetSchemaFileName;
	}

	@Override
	public Map<String, String> getTemplates() {
		return this.templates;
	}

	@Override
	public String getPackageName() {
		// Not used
		return null;
	}

	@Override
	public boolean isAddRelayConnections() {
		return this.addRelayConnections;
	}
}
