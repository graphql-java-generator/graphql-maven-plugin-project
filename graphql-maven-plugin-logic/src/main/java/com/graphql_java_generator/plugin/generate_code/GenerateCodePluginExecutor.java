package com.graphql_java_generator.plugin.generate_code;

import java.io.File;
import java.io.IOException;
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
import com.ibm.icu.text.SimpleDateFormat;

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

	public static final String FILE_TYPE_JACKSON_DESERIALIZER = "Jackson deserializer";

	@Autowired
	GenerateCodeDocumentParser documentParser;

	@Autowired
	GenerateCodeGenerator generator;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GenerateCodeCommonConfiguration configuration;

	/** The component that reads the GraphQL schema from the file system */
	@Autowired
	ResourceSchemaStringProvider resourceSchemaStringProvider;

	@Autowired
	GraphqlUtils graphqlUtils;

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
		if (isSkipCodeGeneration()) {
			logger.info(
					"The GraphQL schema file(s) is(are) older than the generated code. The code generation is skipped.");
		} else {
			// Let's do the job
			documentParser.parseDocuments();
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
								+ configuration.getTemplates().get(key) + "' (ithe full path is '"
								+ file.getCanonicalPath() + "')");
					}

				}
			}
		}
	}

	private boolean isSkipCodeGeneration() throws IOException {
		// Shall we skip the code generation?
		boolean skipCodeGeneration = false;
		if (configuration.isSkipGenerationIfSchemaHasNotChanged()) {
			logger.debug(
					"skipGenerationIfSchemaHasNotChanged is on. Checking the last modification dates of the generated sources");
			skipCodeGeneration = skipGenerationIfSchemaHasNotChanged();
		}
		return skipCodeGeneration;
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
		Long targetSourcesLastModified = graphqlUtils.getLastModified(configuration.getTargetSourceFolder(), false);
		if (targetSourcesLastModified == null) {
			logger.debug("No source folder: we need to generate the sources");
			return false;
		}

		if (logger.isDebugEnabled()) {
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
