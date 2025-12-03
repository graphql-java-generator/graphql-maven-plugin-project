/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.plugin.PluginExecutor;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

/**
 * This class is the super class of all Mojos. It contains all parameters that are common to all goals, and the
 * {@link #execute()} method.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each Mojo.
 * 
 * @author etienne-sf
 */
public abstract class AbstractCommonMojo extends AbstractMojo implements CommonConfiguration {

	/** The Maven {@link BuildContext} that allows to link the build with the IDE */
	@Inject
	protected BuildContext buildContext;

	@Parameter(defaultValue = "${project.build.directory}")
	private File projectBuildDir;

	@Inject
	protected MavenProjectHelper projectHelper;

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.addRelayConnections", defaultValue = CommonConfiguration.DEFAULT_ADD_RELAY_CONNECTIONS)
	boolean addRelayConnections;
	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL unions. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.unionSuffix", defaultValue = CommonConfiguration.DEFAULT_SUFFIX)
	public String unionSuffix;

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL enums. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.enumPrefix", defaultValue = CommonConfiguration.DEFAULT_PREFIX)
	public String enumPrefix;

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL enums. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.enumSuffix", defaultValue = CommonConfiguration.DEFAULT_SUFFIX)
	public String enumSuffix;

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL interfaces. The prefix is
	 * added at the beginning of the java classname, and must be compatible with java naming rules (no space, dot,
	 * comma, etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.interfacePrefix", defaultValue = CommonConfiguration.DEFAULT_PREFIX)
	public String interfacePrefix;

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL interfaces. The suffix is
	 * added at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.interfaceSuffix", defaultValue = CommonConfiguration.DEFAULT_SUFFIX)
	public String interfaceSuffix;

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL input objects. The prefix
	 * is added at the beginning of the java classname, and must be compatible with java naming rules (no space, dot,
	 * comma, etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.inputPrefix", defaultValue = CommonConfiguration.DEFAULT_PREFIX)
	public String inputPrefix;

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL input objects. The suffix
	 * is added at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.javaClassSuffix", defaultValue = CommonConfiguration.DEFAULT_SUFFIX)
	public String inputSuffix;

	@Parameter(property = "com.graphql_java_generator.mavenplugin.jsonGraphqlSchemaFilename", defaultValue = CommonConfiguration.DEFAULT_JSON_GRAPHQL_SCHEMA_FILE)
	public String jsonGraphqlSchemaFilename;

	/**
	 * <I>(Useless, since 1.18.7)</I>Defines the options that maximum number of tokens that the GraphQL schema parser
	 * may read. The default value is Integer.MAX_VALUE (=2147483647). If the schema contains more than
	 * <I>maxTokens</I>, the build will fail with an error.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.maxTokens", defaultValue = CommonConfiguration.DEFAULT_MAX_TOKENS)
	public Integer maxTokens;

	/**
	 * Not available to the user: the MavenProject in which the plugin executes
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFileFolder", defaultValue = GraphQLConfiguration.DEFAULT_SCHEMA_FILE_FOLDER)
	File schemaFileFolder;

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFilePattern", defaultValue = GraphQLConfiguration.DEFAULT_SCHEMA_FILE_PATTERN)
	String schemaFilePattern;

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
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSchemaSubFolder", defaultValue = CommonConfiguration.DEFAULT_TARGET_SCHEMA_SUBFOLDER)
	String targetSchemaSubFolder;

	/**
	 * <P>
	 * This parameter is now <B><I>deprecated</I></B>: it's value used in the plugin is always true, that is: if the
	 * generated sources or resources are older than the GraphQL schema file(s), then there is no source or resource
	 * generation. In clear, the source and resource generation is executed only if the provided input (GraphQL
	 * schema...) has been updated since the last plugin execution.
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.skipGenerationIfSchemaHasNotChanged", defaultValue = GraphQLConfiguration.DEFAULT_SKIP_GENERATION_IF_SCHEMA_HAS_NOT_CHANGED)
	boolean skipGenerationIfSchemaHasNotChanged;

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.templates")
	Map<String, String> templates;

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL types. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.typePrefix", defaultValue = CommonConfiguration.DEFAULT_PREFIX)
	public String typePrefix;

	/**
	 * An optional suffix to add to the classnames of the generated java classes for GraphQL types. The suffix is added
	 * at the end of the java classname, and must be compatible with java naming rules (no space, dot, comma, etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.typeSuffix", defaultValue = CommonConfiguration.DEFAULT_SUFFIX)
	public String typeSuffix;

	/**
	 * An optional prefix to add to the classnames of the generated java classes for GraphQL unions. The prefix is added
	 * at the beginning of the java classname, and must be compatible with java naming rules (no space, dot, comma,
	 * etc.)
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.unionPrefix", defaultValue = CommonConfiguration.DEFAULT_PREFIX)
	public String unionPrefix;

	/**
	 * This class contains the Spring configuration for the actual instance of this Mojo. It's set by subclasses,
	 * through the constructor
	 */
	protected final Class<?> springConfigurationClass;

	/** The Spring context used for the plugin execution. It contains all the beans that runs for its execution */
	protected AnnotationConfigApplicationContext ctx;

	@Override
	public String getEnumPrefix() {
		return enumPrefix;
	}

	@Override
	public String getEnumSuffix() {
		return enumSuffix;
	}

	@Override
	public String getInputPrefix() {
		return inputPrefix;
	}

	@Override
	public String getInputSuffix() {
		return inputSuffix;
	}

	@Override
	public String getInterfacePrefix() {
		return interfacePrefix;
	}

	@Override
	public String getInterfaceSuffix() {
		return interfaceSuffix;
	}

	@Override
	public String getJsonGraphqlSchemaFilename() {
		return jsonGraphqlSchemaFilename;
	}

	@Override
	public Integer getMaxTokens() {
		return maxTokens;
	}

	@Override
	public File getProjectBuildDir() {
		return projectBuildDir;
	}

	@Override
	public File getProjectDir() {
		return project.getBasedir();
	}

	@Override
	public File getProjectMainSourceFolder() {
		return new File(getProjectDir(), "src/main/java");
	}

	@Override
	public File getSchemaFileFolder() {
		return schemaFileFolder;
	}

	@Override
	public String getSchemaFilePattern() {
		return schemaFilePattern;
	}

	@Override
	public String getTargetSchemaSubFolder() {
		return targetSchemaSubFolder;
	}

	@Override
	public Map<String, String> getTemplates() {
		return templates;
	}

	@Override
	public String getTypePrefix() {
		return typePrefix;
	}

	@Override
	public String getTypeSuffix() {
		return typeSuffix;
	}

	@Override
	public String getUnionPrefix() {
		return unionPrefix;
	}

	@Override
	public String getUnionSuffix() {
		return unionSuffix;
	}

	@Override
	public boolean isSkipGenerationIfSchemaHasNotChanged() {
		return skipGenerationIfSchemaHasNotChanged;
	}

	AbstractCommonMojo(Class<?> springConfigurationClass) {
		this.springConfigurationClass = springConfigurationClass;
	}

	/** {@inheritDoc} */
	@Override
	final public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			LoggerFactory.getLogger(getClass()).debug("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			ctx = new AnnotationConfigApplicationContext();
			ctx.getBeanFactory().registerSingleton("mojo", this);
			ctx.register(springConfigurationClass);
			ctx.refresh();

			// Let's log the current configuration (this will do something only when in
			// debug mode)
			ctx.getBean(CommonConfiguration.class).logConfiguration();

			// Let's execute the job
			PluginExecutor executor = ctx.getBean(PluginExecutor.class);
			executor.execute();

			executePostExecutionTask();

			ctx.close();

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void executePostExecutionTask() throws IOException {
		// Default: no action
	}

}
