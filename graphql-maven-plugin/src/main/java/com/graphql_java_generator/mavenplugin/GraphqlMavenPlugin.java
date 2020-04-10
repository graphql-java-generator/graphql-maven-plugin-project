/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;

import graphql.ThreadSafe;
import graphql.schema.GraphQLScalarType;

/**
 * @author EtienneSF
 */
@Mojo(name = "graphql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
@ThreadSafe
public class GraphqlMavenPlugin extends AbstractMojo {

	/**
	 * Flag to enable copy sources for graphql-java-runtime library to target source code directory. It allows to
	 * control whether the runtime code is embedded in the generated code or not. <BR/>
	 * The default behavior is the old one, that is: the runtime code is embedded. This means that when you upgrade the
	 * plugin version, just build the project and everything is coherent.<BR/>
	 * If you set this parameter to false, the runtime is no more copied with the generated code. it's up to you to
	 * provided the runtime in the pom dependencies. This allows you to create your own runtime, and change the
	 * "standard" behavior. But of course, you'll have to check the compatibility with all the next versions.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.copyRuntimeSources", defaultValue = PluginConfiguration.DEFAULT_COPY_RUNTIME_SOURCES)
	boolean copyRuntimeSources;

	/**
	 * This parameter contains the list of custom scalars implementations. One such implementation must be provided for
	 * each custom scalar defined in the GraphQL implemented by the project for its GraphQL schema. It's a list, where
	 * the key is the scalar name, as defined in the GraphQL schema, and the value is the full class name of the
	 * implementation of {@link GraphQLScalarType}. <BR/>
	 * This parameter is a list of customScalars. For each one, you must define the name, the javaType and exactly one
	 * of these fields: graphQLScalarTypeClass, graphQLScalarTypeStaticField or graphQLScalarTypeGetter.<BR/>
	 * Here is the detail:
	 * <UL>
	 * <LI><B>graphQLTypeName: The type name, as defined in the GraphQL schema, for instance <I>Date</I></LI>
	 * <LI><B>javaType: The full class name for the java type that contains the data for this type, once in the Java
	 * code, for instance <I>java.util.Date</I></LI>
	 * <LI><B>graphQLScalarTypeClass</B>: The full class name for the {@link GraphQLScalarType} that will manage this
	 * Custom Scalar. This class must be a subtype of {@link GraphQLScalarType}. Bu the constructor of
	 * {@link GraphQLScalarType} has been deprecated, so you'll find no sample for that in this project</LI>
	 * <LI><B>graphQLScalarTypeStaticField</B>: The full class name followed by the static field name that contains the
	 * {@link GraphQLScalarType} that will manage this Custom Scalar. For instance, the graphql-java package provides
	 * several custom scalars like <I>graphql.Scalars.GraphQLLong</I>. You can also use the
	 * <I>graphql-java-extended-scalars</I> project, that provides other custom scalars like
	 * <I>graphql.scalars.ExtendedScalars.NonNegativeInt</I>.</LI>
	 * <LI><B>graphQLScalarTypeGetter</B>: The full class name followed by the static method name that returns the
	 * {@link GraphQLScalarType} that will manage this Custom Scalar. For instance:
	 * <I>org.mycompany.MyScalars.getGraphQLLong()</I> or
	 * <I>com.graphql_java_generator.customscalars.GraphQLScalarTypeDate</I>. This call may contain parameters, provided
	 * that this a valid java command.</LI>
	 * <UL>
	 * Please have a look at the allGraphQLCases (both client and server) samples for more information. The <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/pom.xml">allGraphQLCases
	 * client pom</A> is a good sample.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.customScalars")
	List<CustomScalarDefinition> customScalars = null;

	/**
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = PluginConfiguration.DEFAULT_GENERATE_JPA_ANNOTATION)
	boolean generateJPAAnnotation;

	Log log;

	/**
	 * The generation mode: either <I>client</I> or <I>server</I>. Choose client to generate the code which can query a
	 * graphql server or server to generate a code for the server side.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.mode", defaultValue = PluginConfiguration.DEFAULT_MODE)
	PluginMode mode;

	/** The package name that will contain the generated classes */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.packageName", defaultValue = PluginConfiguration.DEFAULT_PACKAGE_NAME)
	String packageName;

	/**
	 * Not available to the user: the {@link MavenProject} in which the plugin executes
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;

	/** The folder where the graphql schema file(s) will be searched. The default schema is the main resource folder. */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFileFolder", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_FILE_FOLDER)
	String schemaFileFolder;

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFilePattern", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_FILE_PATTERN)
	String schemaFilePattern;

	/**
	 * schemaPersonalizationFile is the file name where the GraphQL maven plugin will find personalization that it must
	 * apply before generating the code. This applies to the <B>server</B> mode only. See
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/schema_personalization.html">the doc on
	 * the plugin web site</A> for more details.<BR/>
	 * The standard file would be something like /src/main/graphql/schemaPersonalizationFile.json, which avoids to embed
	 * this compile time file within your maven artifact (as it is not in the /src/main/java nor in the
	 * /src/main/resources folders).
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaPersonalizationFile", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE)
	String schemaPersonalizationFile;

	/**
	 * Indicates whether the utility classes (that is: the classes that are not match an item in the GraphQL schema) are
	 * generated in the same package than the classes that matches the GraphQL schema.<BR/>
	 * That is: internal technical classes, java classes that contain the method to execute the
	 * queries/mutations/subscriptions, Jackson deserializer for custom scalars...<BR/>
	 * The default value is false, to maintain the previous behavior. In this case, all classes are generated in the
	 * <I>packageName</I>, or the default package if this parameter is not defined.<BR/>
	 * If true, the GraphQL classes are generated in the package defined in the <I>packageName</I> plugin parameter. And
	 * all the utility classes are generated in the <I>util</I> subpackage of this package.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.separateUtilClasses", defaultValue = PluginConfiguration.DEFAULT_SEPARATE_UTIL_CLASSES)
	boolean separateUtilClasses;

	/** The encoding charset for the generated source files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.sourceEncoding", defaultValue = PluginConfiguration.DEFAULT_SOURCE_ENCODING)
	String sourceEncoding;

	/** The folder where the generated classes will be generated */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSourceFolder", defaultValue = PluginConfiguration.DEFAULT_TARGET_SOURCE_FOLDER)
	String targetSourceFolder;

	/**
	 * Map of the code templates to be used: this allows to override the default templates, and control exactly what
	 * code is generated by the plugin.<BR/>
	 * You can override any of the Velocity templates of the project. The list of templates is defined in the enum
	 * CodeTemplate, that you can <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-logic/src/main/java/com/graphql_java_generator/plugin/CodeTemplate.java">check
	 * here</A>.<BR/>
	 * You can find a sample in the <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-CustomTemplates-client/pom.xml">CustomTemplates
	 * client sample</A>.<BR/>
	 * <B>Important notice:</B> Please note that the default templates may change in the future. And some of these
	 * modifications would need to be reported into the custom templates. We'll try to better expose a stable public API
	 * in the future.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.templates")
	Map<String, String> templates;

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Instant start = Instant.now();
			getLog().debug("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			SpringConfiguration.mojo = this;
			AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

			// Let's log the current configuration (this will do something only when in
			// debug mode)
			ctx.getBean(PluginConfiguration.class).logConfiguration();

			DocumentParser documentParser = ctx.getBean(DocumentParser.class);
			documentParser.parseDocuments();

			CodeGenerator codeGenerator = ctx.getBean(CodeGenerator.class);
			int nbGeneratedClasses = codeGenerator.generateCode();

			ctx.close();

			File targetDir = new File(project.getBasedir(), "target");
			project.addCompileSourceRoot(new File(targetDir, targetSourceFolder).getAbsolutePath());

			Duration duration = Duration.between(start, Instant.now());
			getLog().info(
					nbGeneratedClasses + " java classes have been generated from the schema(s) '" + schemaFilePattern
							+ "' in the package '" + packageName + "' in " + duration.getSeconds() + " seconds");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
