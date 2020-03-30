/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
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
import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;

import graphql.schema.GraphQLScalarType;

/**
 * @author EtienneSF
 */
@Mojo(name = "graphql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
public class GraphqlMavenPlugin extends AbstractMojo {

	/**
	 * List of custom scalars implemented by the project for its GraphQL schema. It's a map, where the key is the scalar
	 * name, as defined in the GraphQL schema, and the value is the full class name of the implementation of
	 * {@link GraphQLScalarType}. <BR/>
	 * Please note that:
	 * <UL>
	 * <LI>for each custom scalar defined in the GraphQL schema, the project must provide one
	 * {@link GraphQLScalarType}</LI>
	 * <LI>The GraphQLScalarType must be describe in this parameter of the maven pom</LI>
	 * <LI></LI>
	 * <UL>
	 * This parameter is a list of customScalars. Each customScalar has these fields:
	 * <UL>
	 * <LI>graphQLTypeName: The type name, as defined in the GraphQL schema, for instance "Date"</LI>
	 * <LI>javaType: The full class name for the java type that contains the data for this type, once in the Java
	 * code</LI>
	 * <LI>graphQLScalarTypeClass: The full class name for the {@link GraphQLScalarType} that will manage this Custom
	 * Scalar. For instance: <I>com.graphql_java_generator.customscalars.GraphQLScalarTypeDate</I>.<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.</LI>
	 * <LI>graphQLScalarTypeStaticField: The full class name followed by the static field name that contains the
	 * {@link GraphQLScalarType} that will manage this Custom Scalar. For instance:
	 * <I>graphql.Scalars.GraphQLLong</I>.<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.</LI>
	 * <LI>graphQLScalarTypeGetter: The full class name followed by the static method name that returns the
	 * {@link GraphQLScalarType} that will manage this Custom Scalar. For instance:
	 * <I>org.mycompany.MyScalars.getGraphQLLong()</I>. This call may contain parameters. Provided that this a valid
	 * java command<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.</LI>
	 * <UL>
	 * Please have a look at the allGraphQLCases (both client and server) samples
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.customScalars")
	List<CustomScalarDefinition> customScalars = null;

	/**
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = "false")
	boolean generateJPAAnnotation;

	/** The packageName in which the generated classes will be created */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.packageName", defaultValue = PluginConfiguration.DEFAULT_PACKAGE_NAME)
	String packageName;

	/** The encoding for the generated source files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.sourceEncoding", defaultValue = PluginConfiguration.DEFAULT_SOURCE_ENCODING)
	String sourceEncoding;

	Log log;

	/**
	 * The generation mode: either client or server. Choose client to generate the code which can query a graphql server
	 * or server to generate a code for the server side.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.mode", defaultValue = PluginConfiguration.DEFAULT_MODE)
	PluginMode mode;

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "src/main/resources" folder. The current version can read only one
	 * file.<BR/>
	 * In the future, it will search for all graphqls file in the root of the classpath.<BR/>
	 * In the future, it will be possible to set in schemaFilePattern values like "myFolder/*.graphqls" to search for
	 * all schemas in the "myFolder" subfolder of src/main/resources (for the plugin execution). At runtime, the path
	 * used for search will then be classpath:/myFolder/*.graphqls".<BR/>
	 * It will also be possible to define one schema, by putting "mySchema.myOtherExtension" in the schemaFilePattern
	 * configuration parameter of the plugin.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFilePattern", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_FILE_PATTERN)
	String schemaFilePattern;

	/** The folder where the graphql schema file(s) will be searched */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaFileFolder", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_FILE_FOLDER)
	String schemaFileFolder;

	/**
	 * schemaPersonalizationFile is the file name where the GraphQL maven plugin will find personalization that it must
	 * apply before generating the code. See the doc for more details.<BR/>
	 * The standard file would be something like src/main/graphql/schemaPersonalizationFile.json, which avoid to embed
	 * this compile time file within your maven artefact<BR/>
	 * The default value is a file named "noPersonalization", meaning: no schema personalization.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaPersonalizationFile", defaultValue = PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE)
	String schemaPersonalizationFile;

	/** The folder where the generated classes will be generated */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSourceFolder", defaultValue = PluginConfiguration.DEFAULT_TARGET_SOURCE_FOLDER)
	String targetSourceFolder;

	/**
	 * Not available to the user: the {@link MavenProject} in which the plugin executes
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;
	
	/**
	 * Map of tempaltes to be used
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.templates")	
	Map<String, String> templates;
	
	/**
	 * Flag to enable copy sources for graphql-java-runtime library to target source code directory
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.copyGraphQLJavaSources", defaultValue = "true")
	boolean copyGraphQLJavaSources;

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

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

			getLog().info(nbGeneratedClasses + " java classes have been generated from the schema(s) '"
					+ schemaFilePattern + "' in the package '" + packageName + "'");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
