/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.QueryMutationExecutionProtocol;

import graphql.schema.GraphQLScalarType;

/**
 * This class is the super class of all Mojos that generates the code, that is the {@link GenerateClientCodeMojo}, the
 * {@link GenerateServerCodeMojo} and the {@link GraphQLMojo} mojos. It contains all parameters that are common to these
 * goals. The parameters common to all goal are inherited from the {@link AbstractCommonMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each
 * 
 * @author etienne-sf
 */
public abstract class AbstractGenerateCodeCommonMojo extends AbstractCommonMojo
		implements GenerateCodeCommonConfiguration {

	int nbGeneratedClasses = 0;

	/**
	 * <P>
	 * Flag to enable (or not) the copy of the sources from the graphql-java-runtime library to target source code
	 * directory. That is: it allows to control whether the runtime code is embedded in the generated code or not.
	 * </P>
	 * <P>
	 * <b>Caution:</b> the default value changed since the 2.0 version. In 1.x version, the default value is true. Since
	 * 2.0 version, the default value is false.
	 * </P>
	 * <UL>
	 * <LI>If copyRuntimeSources=true: the runtime is copied along with the generated code. The project configuration
	 * (pom.xml or build.gradle) must contain the <code>com.graphql-java-generator:graphql-java-dependencies</code>
	 * dependency, with the same version as the GraphQL plugin</LI>
	 * <LI>If copyRuntimeSources=false: the runtime is NOT copied along with the generated code. The project
	 * configuration (pom.xml or build.gradle) must contain the
	 * <code>com.graphql-java-generator:graphql-java-runtime</code> dependency, with the same version as the GraphQL
	 * plugin</LI>
	 * <LI></LI>
	 * </UL>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.copyRuntimeSources", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_COPY_RUNTIME_SOURCES)
	boolean copyRuntimeSources;

	/**
	 * <P>
	 * This parameter contains the list of custom scalars implementations. One such implementation must be provided for
	 * each custom scalar defined in the GraphQL implemented by the project for its GraphQL schema. It's a list, where
	 * the key is the scalar name, as defined in the GraphQL schema, and the value is the full class name of the
	 * implementation of {@link GraphQLScalarType}.
	 * </P>
	 * <P>
	 * This parameter is a list of customScalars. For each one, you must define the name, the javaType and exactly one
	 * of these fields: graphQLScalarTypeClass, graphQLScalarTypeStaticField or graphQLScalarTypeGetter.
	 * </P>
	 * <P>
	 * Here is the detail:
	 * </P>
	 * <UL>
	 * <LI><B>graphQLTypeName</B>: The type name, as defined in the GraphQL schema, for instance <I>Date</I></LI>
	 * <LI><B>javaType</B>: The full class name for the java type that contains the data for this type, once in the Java
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
	 * </UL>
	 * <P>
	 * Please have a look at the allGraphQLCases (both client and server) samples for more information. The <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/pom.xml">allGraphQLCases
	 * client pom</A> is a good sample.
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.customScalars")
	List<CustomScalarDefinition> customScalars = null;

	/** The package name that will contain the generated classes */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.packageName", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_PACKAGE_NAME)
	String packageName;

	/**
	 * (since 2.0RC1) The {@link QueryMutationExecutionProtocol} to use for GraphQL queries and mutations (not
	 * subscriptions). The allowed values are: http and webSocket.<br/>
	 * The default value is http.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.queryMutationExecutionProtocol", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_QUERY_MUTATION_EXECUTION_PROTOCOL)
	QueryMutationExecutionProtocol queryMutationExecutionProtocol;

	/**
	 * <P>
	 * schemaPersonalizationFile is the file name where the GraphQL maven plugin will find personalization that it must
	 * apply before generating the code. Since the 2.2 release, it is available for both client and server. Before, it
	 * applies to the <B>server</B> mode only.
	 * <P>
	 * <P>
	 * This allows to:
	 * <UL>
	 * <LI>Add or modify fields</LI>
	 * <LI>Add interface and annotation to classes (GraphQL types, input types, interfaces, unions and enums) or fields.
	 * </LI>
	 * </UL>
	 * </P>
	 * <P>
	 * See <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/usage_schema_personalization">the
	 * doc on the project's wiki</A> for more details.
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaPersonalizationFile", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE)
	String schemaPersonalizationFile;

	/**
	 * <P>
	 * Indicates whether the utility classes (that is: the classes that are not match an item in the GraphQL schema) are
	 * generated in the same package than the classes that matches the GraphQL schema.
	 * </P>
	 * <P>
	 * The classes that map the GraphQL schema (type, input type, interfaces, unions...) are <b>generated</b> in the
	 * package defined in the <I>packageName</I> plugin parameter, then:
	 * </P>
	 * <ul>
	 * <li>If false <i>(default for versions 1.x)</i>, the utility classes are generated in the the same package</li>
	 * <li>If true <i>(default for version 2.0 and after)</i>, the utility classes are generated in the <I>util</I>
	 * subpackage of this package</li>
	 * </ul>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.separateUtilityClasses", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_SEPARATE_UTIL_CLASSES)
	boolean separateUtilityClasses;

	/** The encoding charset for the generated source files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.sourceEncoding", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_SOURCE_ENCODING)
	String sourceEncoding;

	/**
	 * Retrieves the suffix that will be applied to the name of the Spring Beans that are generated for this schema.
	 * It's mandatory if you' using a Spring app and have more than one GraphQL schemas. The default value is an empty
	 * String.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.springBeanSuffix", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_SPRING_BEAN_SUFFIX)
	String springBeanSuffix;

	/** The folder where resources will be generated */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetResourceFolder", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_TARGET_RESOURCE_FOLDER)
	File targetResourceFolder;

	/** The folder where source code for the generated classes will be generated */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSourceFolder", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_TARGET_SOURCE_FOLDER)
	File targetSourceFolder;

	/**
	 * (since 2.0RC1) If false, it uses jakarta EE8 imports (that begins by javax.). If true, it uses jakarta EE8
	 * imports (that begins by jakarta.).
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.useJakartaEE9", defaultValue = GenerateCodeCommonConfiguration.DEFAULT_USE_JAKARTA_EE9)
	boolean useJakartaEE9;

	@Override
	public List<CustomScalarDefinition> getCustomScalars() {
		return customScalars;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public QueryMutationExecutionProtocol getQueryMutationExecutionProtocol() {
		return queryMutationExecutionProtocol;
	}

	@Override
	public File getSchemaPersonalizationFile() {
		return (GenerateCodeCommonConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE.equals(schemaPersonalizationFile))
				? null
				: new File(project.getBasedir(), schemaPersonalizationFile);
	}

	@Override
	public String getSourceEncoding() {
		return sourceEncoding;
	}

	@Override
	public String getSpringBeanSuffix() {
		return springBeanSuffix;
	}

	public File getTargetFolder() {
		return new File(project.getBasedir(), "target");
	}

	@Override
	public File getTargetClassFolder() {
		return new File(getTargetFolder(), "classes");
	}

	@Override
	public File getTargetResourceFolder() {
		return targetResourceFolder;
	}

	@Override
	public File getTargetSourceFolder() {
		return targetSourceFolder;
	}

	@Override
	public boolean isCopyRuntimeSources() {
		return copyRuntimeSources;
	}

	@Override
	public boolean isSeparateUtilityClasses() {
		return separateUtilityClasses;
	}

	@Override
	public boolean isAddRelayConnections() {
		return addRelayConnections;
	}

	@Override
	public boolean isUseJakartaEE9() {
		return useJakartaEE9;
	}

	protected AbstractGenerateCodeCommonMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}

	@Override
	protected void executePostExecutionTask() throws IOException {
		String generatedSourceFolder = getTargetSourceFolder().getAbsolutePath();
		getLog().debug("Adding the generated source folder: " + generatedSourceFolder);
		project.addCompileSourceRoot(generatedSourceFolder);
		buildContext.refresh(getTargetSourceFolder());
		getLog().debug("compileSourceRoots: " + String.join(", ", project.getCompileSourceRoots()));

		projectHelper.addResource(project, getTargetResourceFolder().getAbsolutePath(), Arrays.asList("**/*"), null);
		buildContext.refresh(getTargetResourceFolder());
	}

}
