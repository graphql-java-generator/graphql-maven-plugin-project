/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.Packaging;
import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * This class is the super class of all Mojos that generates the code, that is the {@link GenerateServerCodeMojo} and
 * the {@link GraphQLMojo} mojos. It contains all parameters that are common to these goals. The parameters common to
 * all goal are inherited from the {@link AbstractGenerateCodeCommonMojo} class.<BR/>
 * This avoids to redeclare each common parameter in each Mojo, including its comment. When a comment is updated, only
 * one update is necessary, instead of updating it in each
 * 
 * @author etienne-sf
 */
public abstract class AbstractGenerateServerCodeMojo extends AbstractGenerateCodeCommonMojo
		implements GenerateServerCodeConfiguration {

	/**
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_JPA_ANNOTATION)
	boolean generateJPAAnnotation;

	/**
	 * <P>
	 * The <I>javaTypeForIDType</I> is the java class that is used in the generated code for GraphQL fields that are of
	 * the GraphQL ID type. The default value is <I>java.util.UUID</I>. Valid values are: java.lang.String,
	 * java.lang.Long and java.util.UUID.
	 * </P>
	 * <P>
	 * This parameter is only valid for the server mode. When generating the client code, the ID is always generated as
	 * a String type, as recommended in the GraphQL doc.
	 * </P>
	 * <P>
	 * In other words: when in server mode and <I>javaTypeForIDType</I> is not set, all GraphQL ID fields are UUID
	 * attributes in java. When in server mode and <I>javaTypeForIDType</I> is set to the X type, all GraphQL ID fields
	 * are X attributes in java.<BR/>
	 * Note: you can override this, by using the schema personalization capability. For more information, please have a
	 * look at the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/schema_personalization.html">Schema
	 * Personalization doc page</A>.
	 * </P>
	 * 
	 * @return
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.javaTypeForIDType", defaultValue = GraphQLConfiguration.DEFAULT_JAVA_TYPE_FOR_ID_TYPE)
	public String javaTypeForIDType;

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

	@Override
	public String getJavaTypeForIDType() {
		return javaTypeForIDType;
	}

	/** The mode is forced to {@link PluginMode#server} */
	@Override
	public PluginMode getMode() {
		return PluginMode.server;
	}

	@Override
	public Packaging getPackaging() {
		return Packaging.valueOf(project.getPackaging());
	}

	@Override
	public File getSchemaPersonalizationFile() {
		return (GraphQLConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE.equals(schemaPersonalizationFile)) ? null
				: new File(project.getBasedir(), schemaPersonalizationFile);
	}

	@Override
	public boolean isGenerateJPAAnnotation() {
		return generateJPAAnnotation;
	}

	@Override
	public String getScanBasePackages() {
		return scanBasePackages;
	}

	protected AbstractGenerateServerCodeMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
