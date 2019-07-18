package com.graphql_java_generator.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Resource
	PluginConfiguration pluginConfiguration;

	public org.springframework.core.io.Resource[] schemas() throws IOException {
		String fullPathPattern = "file:///" + pluginConfiguration.getResourcesFolder().getCanonicalPath()
				+ ((pluginConfiguration.getSchemaFilePattern().startsWith("/")
						|| (pluginConfiguration.getSchemaFilePattern().startsWith("\\"))) ? "" : "/")
				+ pluginConfiguration.getSchemaFilePattern();
		return applicationContext.getResources(fullPathPattern);
	}

	public List<String> schemaStrings() throws IOException {
		org.springframework.core.io.Resource[] resources = schemas();
		if (resources.length <= 0) {
			throw new IllegalStateException("No graphql schema files found on classpath with location pattern '"
					+ pluginConfiguration.getSchemaFilePattern());
		}

		return Arrays.stream(resources).map(this::readSchema).collect(Collectors.toList());
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
