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
 * Overrides the {@link GraphQLJavaToolsAutoConfiguration#schemaStringProvider()} bean, to loads our graphqls files,
 * from the given schemaFilePattern plugin parameter
 * 
 * @author etienne-sf
 */
@Component
public class ResourceSchemaStringProvider {

	private static final Logger logger = LoggerFactory.getLogger(ResourceSchemaStringProvider.class);

	final public String INTROSPECTION_SCHEMA = "classpath:/introspection.graphqls";

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...).
	 * <BR/>
	 * It adds the introspection GraphQL schema to the list of documents to read
	 */
	@Autowired
	CommonConfiguration configuration;

	/**
	 * 
	 * @param addIntrospectionSchema
	 *            true if the introspectionSchema must be added to the list of schemas. (should be true only when
	 *            generated code, in client mode)
	 * @return
	 * @throws IOException
	 */
	public List<org.springframework.core.io.Resource> schemas(boolean addIntrospectionSchema) throws IOException {
		String fullPathPattern;
		if (this.configuration.getSchemaFilePattern().startsWith("classpath:")) {
			// We take the file pattern as is
			fullPathPattern = this.configuration.getSchemaFilePattern();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Before getCanonicalPath(" + this.configuration.getSchemaFileFolder() + ")");
				this.configuration.getSchemaFileFolder().getCanonicalPath();
			}
			fullPathPattern = "file:///" + this.configuration.getSchemaFileFolder().getCanonicalPath()
					+ ((this.configuration.getSchemaFilePattern().startsWith("/")
							|| (this.configuration.getSchemaFilePattern().startsWith("\\"))) ? "" : "/")
					+ this.configuration.getSchemaFilePattern();
		}

		// Let's look for the GraphQL schema files
		List<org.springframework.core.io.Resource> ret = new ArrayList<>(
				Arrays.asList(this.applicationContext.getResources(fullPathPattern)));

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
					+ this.configuration.getSchemaFilePattern() + "', and search folder is '"
					+ this.configuration.getSchemaFileFolder().getCanonicalPath() + "')");
		}

		// In client mode, we need to read the introspection schema
		if (addIntrospectionSchema) {
			ret.add(getIntrospectionSchema());
		}

		return ret;
	}

	/**
	 * Returns a {@link Resource} that points to the Introspection GraphQL schema file
	 * 
	 * @return
	 * @throws IOException
	 */
	Resource getIntrospectionSchema() throws IOException {
		org.springframework.core.io.Resource introspection = this.applicationContext
				.getResource(this.INTROSPECTION_SCHEMA);
		if (!introspection.exists()) {
			throw new IOException("The introspection GraphQL schema doesn't exist (" + this.INTROSPECTION_SCHEMA + ")");
		}
		return introspection;
	}

	/**
	 * Returns the concatenation of all the GraphQL schema, in one string
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getConcatenatedSchemaStrings() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String str : schemaStrings()) {
			sb.append(str);
			sb.append('\n');
		}
		return sb.toString();
	}

	public List<String> schemaStrings() throws IOException {
		// In client mode, we need to read the introspection schema
		boolean readIntrospectionSchema = this.configuration instanceof GenerateCodeCommonConfiguration
				&& ((GenerateCodeCommonConfiguration) this.configuration).getMode().equals(PluginMode.client);

		List<org.springframework.core.io.Resource> resources = schemas(readIntrospectionSchema);
		if (resources.size() == 0) {
			throw new IllegalStateException("No graphql schema files found on classpath with location pattern '"
					+ this.configuration.getSchemaFilePattern());
		}

		return resources.stream().map(this::readSchema).collect(Collectors.toList());
	}

	public String readSchema(org.springframework.core.io.Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

	public String getSchemaFilePattern() {
		return this.configuration.getSchemaFilePattern();
	}

}
