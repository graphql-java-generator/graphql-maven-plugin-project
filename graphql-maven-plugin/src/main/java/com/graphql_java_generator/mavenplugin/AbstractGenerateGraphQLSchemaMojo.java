/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;

import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;

/**
 * @author etienne-sf
 */
public abstract class AbstractGenerateGraphQLSchemaMojo extends AbstractCommonMojo
		implements GenerateGraphQLSchemaConfiguration {

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

	@Component
	protected MavenProjectHelper projectHelper;

	AbstractGenerateGraphQLSchemaMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
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
	public boolean isAddRelayConnections() {
		return this.addRelayConnections;
	}

	@Override
	protected void executePostExecutionTask() throws IOException {
		Resource generatedResources = new Resource();
		String generatedResourceFolder = getTargetFolder().getPath();
		generatedResources.setDirectory(generatedResourceFolder);
		generatedResources.setIncludes(Arrays.asList("**/*"));
		generatedResources.setExcludes(null);
		// One of the two below is probably useless
		project.addResource(generatedResources);
		project.addResource(generatedResources);
		getLog().debug("Added the generated resources folder: " + generatedResourceFolder);
		//
		// Method 2 (should work better):
		projectHelper.addResource(project, generatedResourceFolder, Arrays.asList("**/*"), null);
	}
}
