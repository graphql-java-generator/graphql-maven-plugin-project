/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.GenerateRelaySchemaConfiguration;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = { "com.graphql_java_generator" }, excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
public class GenerateRelaySchemaSpringConfiguration {

	/**
	 * This static field is a trick to let the Spring ApplicationContext access to this instance. If you find any better
	 * solution, let us know !
	 */
	static GenerateRelaySchemaMojo mojo = null;

	@Bean
	GenerateRelaySchemaConfiguration pluginConfiguration() {
		return new GenerateRelaySchemaConfigurationImpl(mojo);
	}

	/**
	 * Loads the schema from the graphqls files. This method uses the GraphQLJavaToolsAutoConfiguration from the
	 * project, to load the schema from the graphqls files
	 * 
	 * @param schemaStringProvider
	 *            The String Provider
	 * @return the {@link Document}s to read
	 * @throws MojoExecutionException
	 *             When an error occurs while reading or parsing the graphql definition files
	 */
	@Bean
	public List<Document> documents(ResourceSchemaStringProvider schemaStringProvider) throws MojoExecutionException {
		try {
			Parser parser = new Parser();
			return schemaStringProvider.schemaStrings().stream().map(parser::parseDocument)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new MojoExecutionException("Error while reading graphql schema definition files: " + e.getMessage(),
					e);
		}

	}
}
