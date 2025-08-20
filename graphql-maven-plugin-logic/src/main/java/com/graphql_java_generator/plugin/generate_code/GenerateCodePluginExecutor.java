package com.graphql_java_generator.plugin.generate_code;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.PluginExecutor;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This class is responsible for the execution of the code generation, so that only a minmal amount of code is in the
 * GraphQL and the Gradle plugins. It is responsible for:
 * <UL>
 * <LI>Check if the plugin logic should be executed (comparison of the date of the provided schema, and the generated
 * sources and resources)</LI>
 * <LI>Parsing of the provided schemas</LI>
 * <LI>Source and resource generation</LI>
 * <UL>
 * 
 * @author etienne-sf
 */
@Component
public class GenerateCodePluginExecutor implements PluginExecutor {

	private static final Logger logger = LoggerFactory.getLogger(PluginExecutor.class);

	@Autowired
	GenerateCodeDocumentParser documentParser;

	@Autowired
	GenerateCodeGenerator generator;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GenerateCodeCommonConfiguration configuration;

	@Autowired
	GraphqlUtils graphqlUtils;

	/** The component that reads the GraphQL schema from the file system */
	@Autowired
	ResourceSchemaStringProvider resourceSchemaStringProvider;

	/**
	 * Actual execution of the goal/task. <BR/>
	 * This method is the method called by the Maven and Gradle plugins. There should be one such component in the
	 * relevant package, for each goal.
	 * 
	 * @throws IOException
	 */
	@Override
	public void execute() throws IOException {
		checkConfiguration();
		if (skipGenerationIfSchemaHasNotChanged()) {
			logger.info(
					"The GraphQL schema file(s) is(are) older than the generated code. The code generation is skipped for target folder "
							+ configuration.getTargetSourceFolder());
		} else {
			// Let's do the job
			documentParser.parseGraphQLSchemas();
			generator.generateCode();
		}
	}

	/**
	 * Do various checks on the given configuration, before starting the work
	 * 
	 * @throws IOException
	 */
	void checkConfiguration() throws IOException {
		if (configuration.getTemplates() != null) {
			for (String key : configuration.getTemplates().keySet()) {
				// Check 1: the key must be a valid template name
				try {
					CodeTemplate.valueOf(key);
				} catch (Exception e) {
					throw new RuntimeException("'" + key + "' is not a valid template name", e);
				}
				// Check 2: the given value must be a valid file
				File file = new File(configuration.getProjectDir(), configuration.getTemplates().get(key));
				if (!file.exists()) {
					// This template is not a local file. So it must be in the classpath.
					if (null == getClass().getClassLoader().getResource(configuration.getTemplates().get(key))) {
						throw new RuntimeException("The file provided for the '" + key
								+ "' template could not be found. The provided filename is: '"
								+ configuration.getTemplates().get(key) + "' (the full path is '"
								+ file.getCanonicalPath() + "')");
					}

				}
			}
		}
	}

	private boolean skipGenerationIfSchemaHasNotChanged() throws IOException {
		long schemaLastModified;

		// First, we look for the last modification date of all the given schema.
		if (configuration.getJsonGraphqlSchemaFilename() != null
				&& !"".equals(configuration.getJsonGraphqlSchemaFilename())) {
			File jsonFile = new File(configuration.getSchemaFileFolder(), configuration.getJsonGraphqlSchemaFilename());
			schemaLastModified = jsonFile.lastModified();
		} else {
			OptionalLong optSchemaLastModification = resourceSchemaStringProvider.schemas(false).stream()//
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
			schemaLastModified = optSchemaLastModification.getAsLong();
		}

		// Then, we get the maximum last modification date for all the generated sources. This makes sure that the code
		// is generated if the schema is newer, and that it is not renegerated even if an old file remains for instance
		// if a type has been removed from the schema (in which case a clean must be done)
		Long targetSourcesLastModified = graphqlUtils.getLastModified(configuration.getTargetSourceFolder(), true);
		if (targetSourcesLastModified == null) {
			logger.debug("No source folder: we need to generate the sources");
			return false;
		}

		if (logger.isInfoEnabled()) {
			Date schemaDate = new Date(schemaLastModified);
			Date targetSourceDate = new Date(targetSourcesLastModified);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
			logger.debug("The lastModified date for the provided schema is: " + formatter.format(schemaDate)
					+ " (more recent date of all provided schemas)");
			logger.debug("The lastModified date for the generated sources is: " + formatter.format(targetSourceDate)
					+ " (older file in all generated sources)");
		}

		// We have the last modification date for both the schema files, and generated sources. We skip the code
		// generation if the generated sources are more recent.
		return schemaLastModified < targetSourcesLastModified;
	}

}
