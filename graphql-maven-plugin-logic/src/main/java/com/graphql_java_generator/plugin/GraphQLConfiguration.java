/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import graphql.schema.GraphQLScalarType;

/**
 * This interface contains all the configuration parameters for the <I>graphql</I> goal (Maven) or task (Gradle) of the
 * plugin, as an interface.<BR/>
 * All these methods are directly the property names, to map against a Spring {@link Configuration} that defines the
 * {@link Bean}s. These beans can then be reused in Spring Component, thank to Spring IoC and its dependency injection
 * capability.
 * 
 * @author etienne-sf
 */
public interface GraphQLConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"

	public final String DEFAULT_COPY_RUNTIME_SOURCES = "true";
	public final String DEFAULT_GENERATE_DEPRECATED_REQUEST_RESPONSE = "true";
	public final String DEFAULT_GENERATE_JPA_ANNOTATION = "false";
	public final String DEFAULT_MODE = "client";
	public final String DEFAULT_PACKAGE_NAME = "com.generated.graphql";
	public final String DEFAULT_SCAN_BASE_PACKAGES = "null";
	public final String DEFAULT_SCHEMA_FILE_FOLDER = "/src/main/resources";
	public final String DEFAULT_SCHEMA_FILE_PATTERN = "*.graphqls";
	public final String DEFAULT_SEPARATE_UTIL_CLASSES = "false";
	public final String DEFAULT_SCHEMA_PERSONALIZATION_FILE = "null"; // Can't by null, must be a valid String.
	public final String DEFAULT_SOURCE_ENCODING = "UTF-8";
	public final String DEFAULT_TARGET_SOURCE_FOLDER = "/generated-sources/graphql-maven-plugin";

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
	 * The logging system to use. It's implemented against the JDK one, to avoid useless dependencies. For instance you
	 * can use log4j2, by adding the 'Log4j JDK Logging Adapter' (JUL)
	 */
	public Logger getLog();

	/**
	 * The generation mode: either client or server. Choose client to generate the code which can query a graphql server
	 * or server to generate a code for the server side.<BR/>
	 * This parameter is mandatory.
	 */
	public PluginMode getMode();

	/** The packageName in which the generated classes will be created */
	public String getPackageName();

	/**
	 * The packaging is the kind of artefact generated by the project. Typically: jar (for a standard Java application)
	 * or war (for a webapp)
	 */
	public Packaging getPackaging();

	/**
	 * <P>
	 * (only for server mode) A comma separated list of package names, <B>without</B> double quotes, that will also be
	 * parsed by Spring, to discover Spring beans, Spring repositories and JPA entities when the server starts. You
	 * should use this parameter only for packages that are not subpackage of the package defined in the _packageName_
	 * parameter and not subpackage of <I>com.graphql_java_generator</I>
	 * </P>
	 * <P>
	 * This allows for instance, to set <I>packageName</I> to <I>your.app.package.graphql</I>, and to define your Spring
	 * beans, like the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/server.html">DataFetcherDelegates</A> or
	 * your Spring data repositories in any other folder, by setting for instance scanBasePackages to
	 * <I>your.app.package.impl, your.app.package.graphql</I>, or just <I>your.app.package</I>
	 * </P>
	 */
	public String getScanBasePackages();

	/**
	 * Transform the list of package names returned by {@link #getScanBasePackages()} by a list of package names,
	 * surrounded by double quotes, as it can be used in the Spring scanBasePackages property of the
	 * {@link SpringBootApplication} Spring annotation.
	 * 
	 * @return A string that can be added to the scanBasePackages property of {@link SpringBootApplication}, that is: an
	 *         empty String, or a list of quoted package names starting with a comma (e.g.: ", \"my.package\",
	 *         \"my.other.package\"")
	 */
	public default String getQuotedScanBasePackages() {
		String scanBasePackages = getScanBasePackages();

		if (scanBasePackages == null || scanBasePackages.contentEquals("") || scanBasePackages.contentEquals("null")) {
			return "";
		}

		// Let's remove all spaces. It will be easier to insert the good double quotes, afterwards.
		// Let's say scanBasePackages is: a, b, c,d
		scanBasePackages = scanBasePackages.replace(" ", "");// scanBasePackages is now a,b,c,d
		scanBasePackages = scanBasePackages.replace(",", "\",\"");// scanBasePackages is now a","b","c","d
		scanBasePackages = ",\"" + scanBasePackages + "\"";// scanBasePackages is now ,"a","b","c","d"
		return scanBasePackages;
	}

	/**
	 * The main resources folder, typically '/src/main/resources' of the current project. That's where the GraphQL
	 * schema(s) are expected to be: in this folder, or one of these subfolders
	 */
	public File getSchemaFileFolder();

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "/src/main/resources" folder (please check also the <I>schemaFileFolder</I>
	 * plugin parameter).
	 */
	public String getSchemaFilePattern();

	/**
	 * <P>
	 * schemaPersonalizationFile is the file name where the GraphQL maven plugin will find personalization that it must
	 * apply before generating the code. This applies to the <B>server</B> mode only. See
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/schema_personalization.html">the doc on
	 * the plugin web site</A> for more details.
	 * </P>
	 * <P>
	 * The standard file would be something like /src/main/graphql/schemaPersonalizationFile.json, which avoids to embed
	 * this compile time file within your maven artifact (as it is not in the /src/main/java nor in the
	 * /src/main/resources folders).
	 * </P>
	 * 
	 * @return
	 */
	public File getSchemaPersonalizationFile();

	/** The encoding for the generated source files */
	public String getSourceEncoding();

	/**
	 * The folder where the generated classes will be compiled, that is: where the class file are stored after
	 * compilation
	 */
	public File getTargetClassFolder();

	/** The folder where the source code for the generated classes will be generated */
	public File getTargetSourceFolder();

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
	 * <P>
	 * <I>Since 1.7.1 version</I>
	 * </P>
	 * <P>
	 * Generates a XxxxResponse class for each query/mutation/subscription, and (if separateUtilityClasses is true) Xxxx
	 * classes in the util subpackage. This allows to keep compatibility with code Developed with the 1.x versions of
	 * the plugin.
	 * </P>
	 * <P>
	 * The best way to use the plugin is to directly use the Xxxx query/mutation/subscription classes, where Xxxx is the
	 * query/mutation/subscription name defined in the GraphQL schema.
	 * </P>
	 */
	public boolean isGenerateDeprecatedRequestResponse();

	/**
	 * <P>
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 * </P>
	 * <P>
	 * <B><I>Default value is false</I></B>
	 * </P>
	 */
	public boolean isGenerateJPAAnnotation();

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

	/** Logs all the configuration parameters, in the debug level */
	public default void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("The graphql-java-generator Plugin Configuration for the graphql goal is:");
			getLog().debug("  copyRuntimeSources: " + isCopyRuntimeSources());
			getLog().debug("  mode: " + getMode());
			getLog().debug("  packageName: " + getPackageName());
			getLog().debug("  packaging: " + getPackaging());
			getLog().debug("  schemaFileFolder: " + getSchemaFileFolder());
			getLog().debug("  schemaFilePattern: " + getSchemaFilePattern());
			getLog().debug("  schemaPersonalizationFile: " + getSchemaPersonalizationFile());
			getLog().debug("  sourceEncoding: " + getMode());
			getLog().debug("  targetClassFolder: " + getTargetClassFolder());
			getLog().debug("  targetSourceFolder: " + getTargetSourceFolder());
			getLog().debug("  Templates: "
					+ (Objects.nonNull(getTemplates())
							? getTemplates().entrySet().stream()
									.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
									.collect(Collectors.joining(", "))
							: StringUtils.EMPTY));
		}
	}
}
