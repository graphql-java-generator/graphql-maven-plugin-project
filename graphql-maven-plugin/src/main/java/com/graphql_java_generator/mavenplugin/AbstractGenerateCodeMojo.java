/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

import graphql.schema.GraphQLScalarType;

/**
 * This class is the super class of all Mojos that generates the code, that is the {@link GenerateClientCodeMojo}, the
 * {@link GenerateServerCodeMojo} and the {@link GraphQLMojo} mojos. It contains all parameters that are common to these
 * goals. The parameters common to all goal are inherited from the {@link AbstractCommonMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each Mojo.
 * 
 * @author etienne-sf
 */
public abstract class AbstractGenerateCodeMojo extends AbstractCommonMojo {

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
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.copyRuntimeSources", defaultValue = GraphQLConfiguration.DEFAULT_COPY_RUNTIME_SOURCES)
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

	/** The encoding charset for the generated source files */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.sourceEncoding", defaultValue = GraphQLConfiguration.DEFAULT_SOURCE_ENCODING)
	String sourceEncoding;

	/** The folder where source code for the generated classes will be generated */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.targetSourceFolder", defaultValue = GraphQLConfiguration.DEFAULT_TARGET_SOURCE_FOLDER)
	String targetSourceFolder;

	protected AbstractGenerateCodeMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
