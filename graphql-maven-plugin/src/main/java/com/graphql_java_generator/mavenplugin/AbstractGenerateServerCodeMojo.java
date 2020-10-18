/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * This class is the super class of all Mojos that generates the code, that is the {@link GenerateServerCodeMojo} and
 * the {@link GraphQLMojo} mojos. It contains all parameters that are common to these goals. The parameters common to
 * all goal are inherited from the {@link AbstractGenerateCodeMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each Mojo.
 * 
 * @author etienne-sf
 */
public abstract class AbstractGenerateServerCodeMojo extends AbstractGenerateCodeMojo {

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateDeprecatedRequestResponse", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_DEPRECATED_REQUEST_RESPONSE)
	boolean generateDeprecatedRequestResponse;

	/**
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_JPA_ANNOTATION)
	boolean generateJPAAnnotation;

	/** The mode is forced to {@link PluginMode#client} */
	protected PluginMode mode = PluginMode.server;

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.scanBasePackages", defaultValue = GraphQLConfiguration.DEFAULT_SCAN_BASE_PACKAGES)
	String scanBasePackages;

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
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.schemaPersonalizationFile", defaultValue = GraphQLConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE)
	String schemaPersonalizationFile;

	protected AbstractGenerateServerCodeMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
