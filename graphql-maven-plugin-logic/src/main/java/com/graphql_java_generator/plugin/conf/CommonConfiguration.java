/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This interface contains all the configuration parameters for the <I>graphql</I> goal (Maven) or task (Gradle) of the
 * plugin, as an interface.<BR/>
 * All these methods are directly the property names, to map against a Spring {@link Configuration} that defines the
 * {@link Bean}s. These beans can then be reused in Spring Component, thank to Spring IoC and its dependency injection
 * capability.
 * 
 * @author etienne-sf
 */
public interface CommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_ADD_RELAY_CONNECTIONS = "false";
	public final String DEFAULT_JSON_GRAPHQL_SCHEMA_FILE = "";
	public final String DEFAULT_MAX_TOKENS = "2147483647"; // Integer.MAX_VALUE
	public final String DEFAULT_PACKAGE_NAME = "com.generated.graphql";
	public final String DEFAULT_PREFIX = "";
	public final String DEFAULT_SCHEMA_FILE_FOLDER = "src/main/resources";
	public final String DEFAULT_SCHEMA_FILE_PATTERN = "*.graphqls";
	public final String DEFAULT_TARGET_SCHEMA_SUBFOLDER = "graphql";
	/**
	 * Subfolder from the root of the classpath, where the schema files are copied, so that spring-graphql can find it
	 * at runtime
	 */
	public final String DEFAULT_SCHEMA_SUB_FOLDER = "graphql";
	public final String DEFAULT_SKIP_GENERATION_IF_SCHEMA_HAS_NOT_CHANGED = "true";
	public final String DEFAULT_SUFFIX = "";

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL enums. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	public String getEnumPrefix();

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL enums. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	public String getEnumSuffix();

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL input objects. The prefix
	 * is added at the beginning of the java classname, and must be compatible with java naming rules (no space, dot,
	 * comma, etc.)
	 */
	public String getInputPrefix();

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL input objects. The suffix
	 * is added at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	public String getInputSuffix();

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL interfaces. The prefix is
	 * added at the beginning of the java classname, and must be compatible with java naming rules (no space, dot,
	 * comma, etc.)
	 */
	public String getInterfacePrefix();

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL interfaces. The suffix is
	 * added at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	public String getInterfaceSuffix();

	/**
	 * <p>
	 * If defined, the plugin loads the GraphQL schema from this json file. This allows to generate the code from the
	 * result of a GraphQL introspection query executed against an existing GraphQL server, for instance if you don't
	 * have its GraphQL schema file.
	 * </p>
	 * <p>
	 * This json file should have been retrieved by the full introspection query. You can find the introspection query
	 * from the <code>getIntrospectionQuery</code> of the
	 * <a href="https://github.com/graphql/graphql-js/blob/main/src/utilities/getIntrospectionQuery.ts">graphql-js</a>
	 * or from this <a href=
	 * "https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/introspection/IntrospectionQuery.java">graphql-java</a>
	 * class. You then have to run it against the GraphQL server, and store the response into a schema.json file.
	 * </p>
	 * 
	 * @return
	 */
	public String getJsonGraphqlSchemaFilename();

	/**
	 * <I>(Useless, since 1.18.7)</I>Defines the options that maximum number of tokens that the GraphQL schema parser
	 * may read. The default value is Integer.MAX_VALUE (=2147483647). If the schema contains more than
	 * <I>maxTokens</I>, the build will fail with an error.
	 * 
	 * @return
	 */
	public Integer getMaxTokens();

	/**
	 * Returns the folder that will contain all project build artefacts. It is typically
	 * <code>{$projectDir}/target</code> for maven and <code>{$projectDir}/build</code> for gradle. But it may be
	 * overridden by project configuration.
	 * 
	 * @return
	 */
	public File getProjectBuildDir();

	/**
	 * Get the {@link File} for the current project's directory. This allows to compute the full path of file that are
	 * within this project (like custom templates for instance)
	 * 
	 * @return
	 */
	public File getProjectDir();

	/**
	 * Get the main source folder for the current project's directory. This allows to check things about the current
	 * project (for instance: does it have a module-info.java file)
	 * 
	 * @return The main source folder (default to ${projectDir}/src/main/java)
	 */
	public File getProjectMainSourceFolder();

	/**
	 * <p>
	 * The folder which contains the GraphQL schema file(s) , typically <code>/src/main/resources</code> of the current
	 * project. That's where the GraphQL schema(s) are expected to be: in this folder, or one of these subfolders. If
	 * the <code>jsonSchemaFilename</code> is set, then this parameter controls where this json schema file is.
	 * </p>
	 * <p>
	 * <u>Caution:</u> this default value for this folder is <code>/src/main/resources</code>, for compatibility with
	 * first versions of this plugin. It's different from the spring-graphql default one, which is
	 * <i>/src/main/resources/graphql</i>
	 * </p>
	 */
	public File getSchemaFileFolder();

	/**
	 * <P>
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 * </P>
	 * <P>
	 * You can put the star (*) joker in the filename, to retrieve several files at ones, for instance
	 * <I>/myschema*.graphqls</I> will retrieve the <I>/src/main/resources/myschema.graphqls</I> and
	 * <I>/src/main/resources/myschema_extend.graphqls</I> files.
	 * <P>
	 */
	public String getSchemaFilePattern();

	/**
	 * <p>
	 * Defines the folder in the classpath that will contain the GraphQL schema, as needed by spring-graphql. The
	 * default is the default for spring-graphql, that is: graphql.
	 * </p>
	 * <p>
	 * Note: If you change this plugin parameter, you must then also define the spring property
	 * spring.graphql.schema.location to "classpath*:yourGraphQLSchemaFolder/, in you application.properties or
	 * application.yml project file.
	 *
	 * </p>
	 * *
	 * <p>
	 * Since 3.0.x
	 * </p>
	 * *
	 * <p>
	 * Mandatory if you're using JPMS (java modules), as the default folder is /graphql, which triggers a conflict with
	 * the graphql package exposed by graphql-java
	 * </p>
	 * 
	 * @return
	 */

	public String getTargetSchemaSubFolder();

	/**
	 * <P>
	 * Map of the code templates to be used: this allows to override the default templates, and control exactly what
	 * code is generated by the plugin.
	 * </P>
	 * <P>
	 * You can override any of the Velocity templates of the project. The list of templates is defined in the enum
	 * CodeTemplate, that you can <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-logic/src/main/java/com/graphql_java_generator/plugin/CodeTemplate.java">check
	 * here</A>.
	 * </P>
	 * <P>
	 * You can find a sample in the <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-CustomTemplates-client/pom.xml">CustomTemplates
	 * client sample</A>.
	 * </P>
	 * <P>
	 * <B>Important notice:</B> Please note that the default templates may change in the future. And some of these
	 * modifications would need to be reported into the custom templates. We'll try to better expose a stable public API
	 * in the future.
	 * </P>
	 */
	public Map<String, String> getTemplates();

	/**
	 * <P>
	 * True if the plugin is configured to add the Relay connection capabilities to the field marked by the
	 * <I>&#064;RelayConnection</I> directive.
	 * </P>
	 * <P>
	 * If so, the plugin reads the provided GraphQL schema file(s), and enriches them with the interfaces and types
	 * needed to respect the Relay Connection specification. The entry point for that is the
	 * <I>&#064;RelayConnection</I> directive.
	 * </P>
	 * <P>
	 * You'll find all the information on the plugin web site. Please check the <A
	 * HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client_add_relay_connection.html>client
	 * Relay capability page</A> or the <A
	 * HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/server_add_relay_connection.html>server
	 * Relay capability page</A>.
	 * </P>
	 */
	public boolean isAddRelayConnections();

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL types. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	public String getTypePrefix();

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL types. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	public String getTypeSuffix();

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL unions. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	public String getUnionPrefix();

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL unions. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	public String getUnionSuffix();

	/**
	 * This method is used only in {@link GeneratePojoConfiguration}.
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns true
	 * @see GeneratePojoConfiguration#isGenerateJacksonAnnotations()
	 */
	default public boolean isGenerateJacksonAnnotations() {
		return true;
	}

	/**
	 * <P>
	 * This parameter is now <B><I>deprecated</I></B>: it's value used in the plugin is always true, that is: if the
	 * generated sources or resources are older than the GraphQL schema file(s), then there is no source or resource
	 * generation. In clear, the source and resource generation is executed only if the provided input (GraphQL
	 * schema...) has been updated since the last plugin execution.
	 * </P>
	 */
	@Deprecated
	public boolean isSkipGenerationIfSchemaHasNotChanged();

	/**
	 * The default name of the target filename.<BR/>
	 * This method must be accessible by the Velocity engine. Thus, it can not be a <I>default</I> interface method.
	 */
	default public String getDefaultTargetSchemaFileName() {
		return GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME;
	}

	/** Logs all the configuration parameters (only when in the debug level) */
	public void logConfiguration();

	/** Logs all the common configuration parameters (only when in the debug level) */
	public default void logCommonConfiguration() {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("  Common parameters:");
			logger.debug("    addRelayConnections: " + isAddRelayConnections());
			logger.debug("    defaultTargetSchemaFileName: " + getDefaultTargetSchemaFileName());
			logger.debug("    jsonGraphqlSchemaFilename: " + getJsonGraphqlSchemaFilename());
			logger.debug("    parserOptions.maxTokens: " + getMaxTokens());
			logger.debug("    projectDir: " + getProjectDir().getAbsolutePath());
			logger.debug("    schemaFileFolder: " + getSchemaFileFolder());
			logger.debug("    schemaFilePattern: " + getSchemaFilePattern());
			logger.debug("    skipGenerationIfSchemaHasNotChanged: " + isSkipGenerationIfSchemaHasNotChanged());
			logger.debug("    Templates: "
					+ (Objects.nonNull(getTemplates())
							? getTemplates().entrySet().stream()
									.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
									.collect(Collectors.joining(", "))
							: ""));
			logger.debug("    enumPrefix: " + getEnumPrefix());
			logger.debug("    enumSuffix: " + getEnumSuffix());
			logger.debug("    inputPrefix: " + getInputPrefix());
			logger.debug("    inputSuffix: " + getInputSuffix());
			logger.debug("    interfacePrefix: " + getInterfacePrefix());
			logger.debug("    interfaceSuffix: " + getInterfaceSuffix());
			logger.debug("    typePrefix: " + getTypePrefix());
			logger.debug("    typeSuffix: " + getTypeSuffix());
			logger.debug("    unionPrefix: " + getUnionPrefix());
			logger.debug("    unionSuffix: " + getUnionSuffix());
		}
	}

}
