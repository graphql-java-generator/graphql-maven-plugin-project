/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.GenerateRelaySchemaConfiguration;

import graphql.ThreadSafe;

/**
 * @author etienne-sf
 */
// @Mojo(name = "generate-relay-schema", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
@ThreadSafe
public class GenerateRelaySchemaMojo extends AbstractMojo {

	/**
	 * The main resources folder, typically '/src/main/resources' of the current project. That's where the GraphQL
	 * schema(s) are expected to be: in this folder, or one of these subfolders
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFileFolder", defaultValue = GenerateRelaySchemaConfiguration.DEFAULT_SCHEMA_FILE_FOLDER)
	File schemaFileFolder;
	/**
	 * The name of the target filename, in which the schema is generated. This file is stored in the folder, defined in
	 * the <I>schemaFileFolder</I> plugin parameter.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFileName", defaultValue = GenerateRelaySchemaConfiguration.DEFAULT_SCHEMA_FILE_NAME)
	String schemaFileName;

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFilePattern", defaultValue = GenerateRelaySchemaConfiguration.DEFAULT_SCHEMA_FILE_PATTERN)
	String schemaFilePattern;

	/** The encoding for the generated resource files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.resourceEncoding", defaultValue = GenerateRelaySchemaConfiguration.DEFAULT_RESOURCE_ENCODING)
	String resourceEncoding;

	/** The folder where the generated GraphQL schema will be stored */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetFolder", defaultValue = GenerateRelaySchemaConfiguration.DEFAULT_TARGET_FOLDER)
	File targetFolder;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().debug("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			GenerateRelaySchemaSpringConfiguration.mojo = this;
			AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
					GenerateRelaySchemaSpringConfiguration.class);

			// Let's log the current configuration (this will do something only when in debug mode)
			ctx.getBean(GenerateRelaySchemaConfiguration.class).logConfiguration();

			DocumentParser documentParser = ctx.getBean(DocumentParser.class);
			documentParser.parseDocuments();

			ctx.close();

			throw new RuntimeException("Not yet implemented");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
