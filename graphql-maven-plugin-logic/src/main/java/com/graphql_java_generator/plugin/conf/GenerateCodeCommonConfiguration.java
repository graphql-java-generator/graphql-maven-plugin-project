/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLScalarType;

/**
 * This class contains all parameters that are common to the <I>generateClientCode</I>, <I>generateServerCode</I> and
 * <I>graphql</I> goals/tasks.
 * 
 * @author etienne-sf
 */
public interface GenerateCodeCommonConfiguration extends CommonConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_COPY_RUNTIME_SOURCES = "true";
	public final String DEFAULT_QUERY_MUTATION_EXECUTION_PROTOCOL = "http";
	public final String DEFAULT_SEPARATE_UTIL_CLASSES = "false";
	public final String DEFAULT_SOURCE_ENCODING = "UTF-8";
	public final String DEFAULT_SPRING_BEAN_SUFFIX = "";
	public final String DEFAULT_TARGET_RESOURCE_FOLDER = "./target/generated-resources/graphql-maven-plugin";
	public final String DEFAULT_TARGET_SOURCE_FOLDER = "./target/generated-sources/graphql-maven-plugin";

	/**
	 * Determines the package that contains the Spring auto configuration class, that is generated for each GraphQL
	 * schema, based on the given parameters
	 * 
	 * @param separateUtilityClasses
	 * @param packageName
	 * @return
	 */
	public static String getSpringAutoConfigurationPackage(boolean separateUtilityClasses, String packageName) {
		if (separateUtilityClasses) {
			// The Spring auto-configuration file can be in subpackage of the provided package.
			return packageName + ".spring_autoconfiguration";
		} else {
			// When all classes are generated in the same package, the Spring auto configuration class must be generated
			// in a package that is not a subpackage of the main one (otherwise it will be read as a standard
			// configuration class, and the @ConditionalOnMissingBean annotation will not work properly)
			return packageName + "_spring_autoconfiguration";
		}
	}

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
	public List<CustomScalarDefinition> getCustomScalars();

	/**
	 * The generation mode: either client or server. Choose client to generate the code which can query a graphql server
	 * or server to generate a code for the server side.<BR/>
	 * This parameter is mandatory. It forced to {@link PluginMode.client} by the <I>generateClientCode</I> goal/task an
	 * to {@link PluginMode.server} by the <I>generateServerCode</I> goal/task.
	 */
	public PluginMode getMode();

	/** The packageName in which the generated classes will be created */
	public String getPackageName();

	/**
	 * The {@link QueryMutationExecutionProtocol} to use for GraphQL queries and mutations (not subscriptions). The
	 * allowed values are: http and webSocket.<br/>
	 * The default value is http.
	 */
	public QueryMutationExecutionProtocol getQueryMutationExecutionProtocol();

	/** The encoding for the generated source files */
	public String getSourceEncoding();

	/**
	 * Returns the package that contains the Spring Auto Configuration class. This package may not be the package that
	 * contains the executor, nor one of its subpackages, so that the configuration class is not read as a standard
	 * Spring configuration class.
	 */
	default public String getSpringAutoConfigurationPackage() {
		return GenerateCodeCommonConfiguration.getSpringAutoConfigurationPackage(isSeparateUtilityClasses(),
				getPackageName());
	}

	/**
	 * Retrieves the suffix that will be applied to the name of the Spring Beans that are generated for this schema.
	 * It's mandatory if you' using a Spring app and have more than one GraphQL schemas. The default value is an empty
	 * String.
	 */
	public String getSpringBeanSuffix();

	/**
	 * The folder where the generated classes will be compiled, that is: where the class file are stored after
	 * compilation
	 */
	public File getTargetClassFolder();

	/** The folder where the generated resources will be generated */
	public File getTargetResourceFolder();

	/** The folder where the source code for the generated classes will be generated */
	public File getTargetSourceFolder();

	/**
	 * <P>
	 * Flag to enable copy sources for graphql-java-runtime library to target source code directory. It allows to
	 * control whether the runtime code is embedded in the generated code or not.
	 * </P>
	 * <P>
	 * The default behavior is the old one, that is: the runtime code is embedded. This means that when you upgrade the
	 * plugin version, just build the project and everything is coherent.
	 * </P>
	 * <P>
	 * If you set this parameter to false, the runtime is no more copied with the generated code. You then have to add
	 * the runtime dependency in the pom dependencies: it's the com.graphql-java-generator:graphql-java-runtime
	 * dependency, with the exact same version as the plugin version.
	 * </P>
	 * <P>
	 * This also allows you to create your own runtime, and change the "standard" behavior. But of course, you'll have
	 * to check the compatibility with all the next versions.
	 * </P>
	 *
	 * @return
	 */
	public boolean isCopyRuntimeSources();

	/**
	 * Indicates whether the utility classes should be generated. If false, only the POJO are generated, that is: only
	 * the Java classes and interfaces that match the GraphQL provided schema. There is no runtime sources for this
	 * goal.
	 * 
	 * @return true if all classes should be generated, false if only the POJO classes should be generated
	 */
	default public boolean isGenerateUtilityClasses() {
		return true;
	}

	/**
	 * <P>
	 * Indicates whether the utility classes (that is: the classes that are not match an item in the GraphQL schema) are
	 * generated in the same package than the classes that matches the GraphQL schema.
	 * </P>
	 * <P>
	 * That is: internal technical classes, java classes that contain the method to execute the
	 * queries/mutations/subscriptions, Jackson deserializer for custom scalars...
	 * </P>
	 * <P>
	 * The default value is false, to maintain the previous behavior. In this case, all classes are generated in the
	 * <I>packageName</I>, or the default package if this parameter is not defined.
	 * </P>
	 * <P>
	 * If true, the GraphQL classes are generated in the package defined in the <I>packageName</I> plugin parameter. And
	 * all the utility classes are generated in the <I>util</I> subpackage of this package.
	 * </P>
	 * 
	 * @return
	 */
	public boolean isSeparateUtilityClasses();

	/** Logs all the configuration parameters (only when in the debug level) */
	@Override
	public void logConfiguration();

	public default void logGenerateCodeCommonConfiguration() {
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.debug("  Common parameters for code generation:");
		logger.debug("    copyRuntimeSources: " + isCopyRuntimeSources());
		logger.debug("    customScalars: " + getCustomScalars());
		logger.debug("    generateUtilityClasses: " + isGenerateUtilityClasses());
		logger.debug("    mode: " + getMode());
		logger.debug("    packageName: " + getPackageName());
		logger.debug("    queryMutationExecutionProtocol: " + getQueryMutationExecutionProtocol());
		logger.debug("    separateUtilityClasses: " + isSeparateUtilityClasses());
		logger.debug("    sourceEncoding: " + getSourceEncoding());
		logger.debug("    springBeanSuffix: " + getSpringBeanSuffix());
		logger.debug("    targetClassFolder: " + getTargetClassFolder().getAbsolutePath());
		logger.debug("    targetResourceFolder: " + getTargetResourceFolder().getAbsolutePath());
		logger.debug("    targetSourceFolder: " + getTargetSourceFolder().getAbsolutePath());
		logCommonConfiguration();
	}

}
