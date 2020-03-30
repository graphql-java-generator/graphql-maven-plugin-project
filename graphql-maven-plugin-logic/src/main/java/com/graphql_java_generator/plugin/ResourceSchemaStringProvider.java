package com.graphql_java_generator.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 
 */

/**
 * Overrides the {@link GraphQLJavaToolsAutoConfiguration#schemaStringProvider()} bean, to loads our graphqls files,
 * from the given schemaFilePattern plugin parameter
 * 
 * @author EtienneSF
 */
@Component
public class ResourceSchemaStringProvider {

	final String INTROSPECTION_SCHEMA = "classpath:/introspection.graphqls";

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...).
	 * <BR/>
	 * It adds the introspection GraphQL schema to the list of documents to read
	 */
	@Resource
	PluginConfiguration pluginConfiguration;

	public List<org.springframework.core.io.Resource> schemas() throws IOException {
		String fullPathPattern;
		if (pluginConfiguration.getSchemaFilePattern().startsWith("classpath:")) {
			// We take the file pattern as is
			fullPathPattern = pluginConfiguration.getSchemaFilePattern();
		} else {
			fullPathPattern = "file:///" + pluginConfiguration.getSchemaFileFolder().getCanonicalPath()
					+ ((pluginConfiguration.getSchemaFilePattern().startsWith("/")
							|| (pluginConfiguration.getSchemaFilePattern().startsWith("\\"))) ? "" : "/")
					+ pluginConfiguration.getSchemaFilePattern();
		}

		List<org.springframework.core.io.Resource> ret = new ArrayList<>(
				Arrays.asList(applicationContext.getResources(fullPathPattern)));

		// In client mode, we need to read the introspection schema
		if (pluginConfiguration.getMode().equals(PluginMode.client)) {
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
					+ pluginConfiguration.getSchemaFilePattern());
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
		return pluginConfiguration.getSchemaFilePattern();
	}
}
