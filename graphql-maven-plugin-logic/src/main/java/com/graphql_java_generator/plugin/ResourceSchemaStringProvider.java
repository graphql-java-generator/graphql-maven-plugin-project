package com.graphql_java_generator.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * 
 */

/**
 * Overrides the {@link GraphQLJavaToolsAutoConfiguration#schemaStringProvider()} bean, to loads our graphqls files,
 * from the given schemaFilePattern plugin parameter
 * 
 * @author etienne-sf
 */
@Component
public class ResourceSchemaStringProvider {

	private static final Logger logger = LoggerFactory.getLogger(ResourceSchemaStringProvider.class);

	final String INTROSPECTION_SCHEMA = "classpath:/introspection.graphqls";

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...).
	 * <BR/>
	 * It adds the introspection GraphQL schema to the list of documents to read
	 */
	@Autowired
	CommonConfiguration configuration;

	public List<org.springframework.core.io.Resource> schemas() throws IOException {
		String fullPathPattern;
		if (configuration.getSchemaFilePattern().startsWith("classpath:")) {
			// We take the file pattern as is
			fullPathPattern = configuration.getSchemaFilePattern();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Before getCanonicalPath(" + configuration.getSchemaFileFolder() + ")");
				configuration.getSchemaFileFolder().getCanonicalPath();
			}
			fullPathPattern = "file:///" + configuration.getSchemaFileFolder().getCanonicalPath()
					+ ((configuration.getSchemaFilePattern().startsWith("/")
							|| (configuration.getSchemaFilePattern().startsWith("\\"))) ? "" : "/")
					+ configuration.getSchemaFilePattern();
		}

		// Let's look for the GraphQL schema files
		List<org.springframework.core.io.Resource> ret = new ArrayList<>(
				Arrays.asList(applicationContext.getResources(fullPathPattern)));

		// A little debug may be useful
		if (logger.isDebugEnabled()) {
			if (ret.size() == 0) {
				logger.debug("No GraphQL schema file found (with this fullPathPattern: '" + fullPathPattern + "'");
			} else {
				logger.debug(
						"The GraphQL schema files found (with this fullPathPattern: '" + fullPathPattern + "') are: ");
				for (Resource schema : ret) {
					logger.debug("   * " + schema.getURI().toString());
				}
			}
		}

		// We musts have found at least one schema
		if (ret.size() == 0) {
			throw new RuntimeException("No GraphQL schema found (the searched file pattern is: '"
					+ configuration.getSchemaFilePattern() + "')");
		}

		// In client mode, we need to read the introspection schema
		if (configuration instanceof GenerateCodeCommonConfiguration
				&& ((GenerateCodeCommonConfiguration) configuration).getMode().equals(PluginMode.client)) {
			org.springframework.core.io.Resource introspection = applicationContext.getResource(INTROSPECTION_SCHEMA);
			if (!introspection.exists()) {
				throw new IOException("The introspection GraphQL schema doesn't exist (" + INTROSPECTION_SCHEMA + ")");
			}
			ret.add(introspection);
		}

		return ret;
	}

	public List<String> schemaStrings() throws IOException {
		List<org.springframework.core.io.Resource> resources = schemas();
		if (resources.size() == 0) {
			throw new IllegalStateException("No graphql schema files found on classpath with location pattern '"
					+ configuration.getSchemaFilePattern());
		}

		return resources.stream().map(this::readSchema).collect(Collectors.toList());
	}

	private String readSchema(org.springframework.core.io.Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

	public String getSchemaFilePattern() {
		return configuration.getSchemaFilePattern();
	}
}
