package com.graphql_java_generator.plugin.generate_schema;

import java.io.IOException;
import java.util.Date;
import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.PluginBuildContext;
import com.graphql_java_generator.plugin.PluginExecutor;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.util.GraphqlUtils;
import com.ibm.icu.text.SimpleDateFormat;

@Component
public class GenerateGraphQLSchemaPluginExecutor implements PluginExecutor {

	private static final Logger logger = LoggerFactory.getLogger(GenerateGraphQLSchemaPluginExecutor.class);

	@Autowired
	GenerateGraphQLSchemaDocumentParser documentParser;

	@Autowired
	GenerateGraphQLSchema generateGraphQLSchema;

	@Autowired
	GenerateGraphQLSchemaConfiguration configuration;

	/** The component that reads the GraphQL schema from the file system */
	@Autowired
	ResourceSchemaStringProvider resourceSchemaStringProvider;

	@Autowired
	GraphqlUtils graphqlUtils;

	/**
	 * The {@link PluginBuildContext} allows to check if the source files have changed, and thus if the code generation
	 * must be executed or not.
	 */
	@Autowired
	PluginBuildContext PluginBuildContext;

	@Override
	public void execute() throws Exception {
		if (PluginBuildContext.hasDelta(resourceSchemaStringProvider.schemas())
				&& !skipGenerationIfSchemaHasNotChanged()) {
			// Let's do the job
			documentParser.parseDocuments();
			generateGraphQLSchema.generateGraphQLSchema();
		} else {
			logger.info(
					"The GraphQL schema file(s) is(are) older than the generated code. The code generation is skipped.");
		}
	}

	private boolean skipGenerationIfSchemaHasNotChanged() throws IOException {

		// First, we look for the last modification date of all the given schema.
		OptionalLong optSchemaLastModification = resourceSchemaStringProvider.schemas().stream()//
				.mapToLong((r) -> {
					try {
						return r.lastModified();
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				})//
				.max();
		if (!optSchemaLastModification.isPresent()) {
			logger.warn(
					"No schema found when checking their lasModified date! (let's got to the generate source process)");
			return false;
		}
		long schemaLastModified = optSchemaLastModification.getAsLong();

		// Then, we get the minimum last modification date for all the generated sources (this insure that the code is
		// regenerated as needed, even if a file has been manually updated.
		Long targetResourcesLastModified = graphqlUtils.getLastModified(configuration.getTargetFolder(), false);
		if (targetResourcesLastModified == null) {
			logger.debug("No source folder: we need to generate the target schema");
			return false;
		}

		if (logger.isDebugEnabled()) {
			Date schemaDate = new Date(schemaLastModified);
			Date targetSourceDate = new Date(targetResourcesLastModified);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
			logger.debug("The lastModified date for the provided schema is: " + formatter.format(schemaDate)
					+ " (more recent date of all provided schemas)");
			logger.debug("The lastModified date for the generated resources is: " + formatter.format(targetSourceDate)
					+ " (older file in all generated resources)");
		}

		// We have the last modification date for both the schema files, and generated the schemas. We skip the code
		// generation if the generated schema files are more recent.
		return schemaLastModified < targetResourcesLastModified;
	}
}
