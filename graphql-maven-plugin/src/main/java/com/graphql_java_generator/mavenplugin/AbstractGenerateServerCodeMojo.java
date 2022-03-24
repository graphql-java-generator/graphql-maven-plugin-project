/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.maven.plugins.annotations.Parameter;
import org.dataloader.BatchLoaderEnvironment;

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
	 * <P>
	 * (only for server mode) Indicates if the plugin should generate add the {@link BatchLoaderEnvironment} parameter
	 * to the <I>batchLoader</I> methods, in DataFetchersDelegate. This parameter allows to get the context of the Batch
	 * Loader, including the context associated to the id, when using the id has been added by the
	 * {@link org.dataloader.DataLoader#load(Object, Object)} method.
	 * </P>
	 * <P>
	 * For instance, if you have the method below, for a field named <I>oneWithIdSubType</I> in a DataFetcherDelegate:
	 * </P>
	 * 
	 * <PRE>
	 * &#64;Override
	 * public CompletableFuture&lt;AllFieldCasesWithIdSubtype> oneWithIdSubType(
	 * 		DataFetchingEnvironment dataFetchingEnvironment, DataLoader&lt;UUID, AllFieldCasesWithIdSubtype> dataLoader,
	 * 		AllFieldCases source, Boolean uppercase) {
	 * 	return dataLoader.load(UUID.randomUUID());
	 * }
	 * </PRE>
	 * <P>
	 * then, in the <I>AllFieldCasesWithIdSubtype</I> DataFetcherDelegate, you can retrieve the uppercase this way:
	 * </P>
	 * 
	 * <PRE>
	 * &#64;Override
	 * public List&lt;AllFieldCasesWithIdSubtype> batchLoader(List&lt;UUID> keys, BatchLoaderEnvironment environment) {
	 * 	List&lt;AllFieldCasesWithIdSubtype> list = new ArrayList<>(keys.size());
	 * 	for (UUID id : keys) {
	 * 		// Let's manage the uppercase parameter, that was associated with this key
	 * 		Boolean uppercase = (Boolean) environment.getKeyContexts().get(id);
	 * 		if (uppercase != null && uppercase) {
	 * 			item.setName(item.getName().toUpperCase());
	 * 		}
	 * 
	 * 		// Do something with the id and the uppercase value
	 * 	}
	 * 	return list;
	 * }
	 * </PRE>
	 * <P>
	 * For more complex cases, you can store a {@link Map} with all the needed values, instead of just the parameter
	 * value.
	 * </P>
	 * <P>
	 * <B><I>Default value is false</I></B>
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateBatchLoaderEnvironment", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_BATCH_LOADER_ENVIRONMENT)
	public boolean generateBatchLoaderEnvironment;

	/**
	 * <P>
	 * (only for server mode) Indicates whether the plugin should generate the JPA annotations, for generated objects.
	 * </P>
	 * <P>
	 * <B><I>Default value is false</I></B>
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_JPA_ANNOTATION)
	boolean generateJPAAnnotation;

	/**
	 * <P>
	 * (only for server mode) Defines how the methods in the data fetchers delegates are generated.
	 * </P>
	 * <P>
	 * When generateDataLoaderForLists is false (default mode), the data loaders are used only for fields that don't
	 * return a list. In other words, for fields which type is a sub-object with an id, two methods are generated: one
	 * which returns a {@link CompletableFuture}, and one which returns a none {@link CompletableFuture} result (that is
	 * used by the generated code only if no data loader is available).
	 * </P>
	 * <P>
	 * When generateDataLoaderForLists is true, there is one getter for field of this data fetcher. If the field's type
	 * is a type with an id (whether it is a list or not), then the return type is a {@link CompletableFuture}. If the
	 * field's type is a type that has no id, then a method is generated with a direct return (not a
	 * {@link CompletableFuture}). When the field's type is a scalar, then no method is generated (no need to fetch it)
	 * </P>
	 * <P>
	 * This parameter is available since version 1.18.4
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.legacyDataLoaderCall", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_DATA_LOADER_FOR_LISTS)
	boolean generateDataLoaderForLists;

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
	 * are X attributes in java.
	 * </P>
	 * <P>
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
	public boolean isGenerateBatchLoaderEnvironment() {
		return generateBatchLoaderEnvironment;
	}

	@Override
	public boolean isGenerateJPAAnnotation() {
		return generateJPAAnnotation;
	}

	@Override
	public boolean isGenerateDataLoaderForLists() {
		return generateDataLoaderForLists;
	}

	@Override
	public String getScanBasePackages() {
		return scanBasePackages;
	}

	protected AbstractGenerateServerCodeMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}
}
