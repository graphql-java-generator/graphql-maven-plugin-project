/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.maven.plugins.annotations.Parameter;
import org.dataloader.BatchLoaderEnvironment;

import com.graphql_java_generator.plugin.conf.BatchMappingDataFetcherReturnType;
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
	 * This parameter is used only when generateBatchMappingDataFetchers is set to <i>true</i>. It determines the return
	 * type of the data fetchers, as defined in the <a href=
	 * "https://docs.spring.io/spring-graphql/reference/controllers.html#controllers.batch-mapping.return.values">spring-graphql
	 * documentation</a>.
	 * </P>
	 * <P>
	 * The allowed values are (where K is the key type, that is: the parent object, and V is the value to be loaded in
	 * batch):
	 * </P>
	 * <TABLE>
	 * <ROW>
	 * <TH>Value</TH>
	 * <TH>Return type</TH></ROW> <ROW>
	 * <TD>MONO_MAP</TD></TD>Mono&lt;Map&lt;K,V&gt;&gt;</TD></ROW> <ROW>
	 * <TD>MAP</TD>
	 * <TD>Map&lt;K,V&gt;</TD></ROW> <ROW>
	 * <TD>FLUX></TD>
	 * <TD>Flux&lt;V&gt;</TD></ROW> <ROW>
	 * <TD>COLLECTION</TD>
	 * <TD>Collection&lt;V&gt;</TD></ROW>
	 * </TABLE>
	 * <P>
	 * The default value is <code>Flux&lt;V&gt;</code>
	 * </P>
	 * <P>
	 * For an easier use of this parameter, the comment of the generated data fetchers details the exact expected type.
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.batchMappingDataFetcherReturnType", defaultValue = GraphQLConfiguration.DEFAULT_BATCH_MAPPING_DATA_FETCHER_RETURN_TYPE_STR)
	public BatchMappingDataFetcherReturnType batchMappingDataFetcherReturnType;

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
	 * <B><I>The default value changed since 2.0 version: it is false in 1.x version, and true since the 2.0
	 * version</I></B>
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateBatchLoaderEnvironment", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_BATCH_LOADER_ENVIRONMENT)
	public boolean generateBatchLoaderEnvironment;

	/**
	 * <P>
	 * If this parameter is set to <i>true</i>, the spring GraphQL controller methods will be annotated with the
	 * <code>@BatchMapping</code> (instead of the <code>@SchemaMapping</code>). This allows to manage the of the N+1
	 * select problem: so this allows much better performances, by highly diminishing the number of executed requests
	 * (avoid to execute several times the same "sub-query")
	 * <P>
	 * </P>
	 * When setting this parameter to <i>true</i>, the main changes are:
	 * </P>
	 * <UL>
	 * <LI>The <code>@BatchMapping</code> annotation may be applied to all data fetchers without argument(s) that return
	 * either a List, a Type, an Interface or an Union.</LI>
	 * <LI>The return type must be defined in the controller: it may not be `Object`, as spring-graphql builds the
	 * proper BatchLoader while loading the controllers, when the server starts. The return type for this method is
	 * managed by the <code>batchMappingMethodReturnType</code> plugin parameter</LI>
	 * <LI>DataLoader is managed transparently by spring (instead of having to declare it in the generated controller,
	 * and having it as a parameter in the generated data fetchers)</LI>
	 * <LI>The batch mapping is generalized on all data fetchers</LI>
	 * <LI>The <code>DataFetchersDelegate</code> method's signature changes</LI>
	 * <LI>The <code>generateBatchLoaderEnvironment</code>, <code>generateDataFetcherForEveryFieldsWithArguments</code>
	 * and <code>generateDataLoaderForLists</code> plugin parameters are ignored</LI>
	 * </UL>
	 * <P>
	 * A typical method signature for a data fetcher would be as below, where the return type is controller by the
	 * <code>batchMappingMethodReturnType</code> plugin parameter :
	 * </P>
	 * 
	 * <PRE>
	 * public Flux<Topic> topics(//
	 * 		BatchLoaderEnvironment batchLoaderEnvironment, //
	 * 		GraphQLContext graphQLContext, //
	 * 		List<Board> boards);
	 * </PRE>
	 * <P>
	 * Please note that the <code>@BatchMapping</code> annotation is a shortcut to avoid boilerplate code, for the most
	 * common cases. See <a href="https://github.com/spring-projects/spring-graphql/issues/232">this discussion</a> for
	 * more information on this. For most complex cases, the use of a DataLoader is recommended by the spring-graphql
	 * case. And in these cases, the plugin will generate a method with the <code>@SchemaMapping</code> annotation
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateBatchMappingDataFetchers", defaultValue = GenerateServerCodeConfiguration.DEFAULT_GENERATE_BATCH_MAPPING_DATA_FETCHERS)
	public boolean generateBatchMappingDataFetchers;

	/**
	 * <P>
	 * (only for server mode, since 2.5) Defines if a data fetcher is needed for every GraphQL field that has input
	 * argument, and add them in the generated POJOs. This allows a better compatibility with spring-graphql, and an
	 * easy access to the field's parameters.
	 * </P>
	 * <P>
	 * This parameter is <b>forced to true</b> when the <code>generateBatchMappingDataFetchers</code> parameter is set
	 * to <i>true</i>.
	 * </P>
	 * <P>
	 * With this argument to false, the data fetchers are generated only for field which type is a type (not a scalar or
	 * an enum), and for the query, mutation and subscription types.
	 * </P>
	 * <P>
	 * With this argument to true, the data fetchers are generated for all GraphQL fields which type is a type (not a
	 * scalar or an enum) <b><i>or</i></b> that has one or arguments
	 * </P>
	 * <P>
	 * This parameter is available since version 2.5:
	 * </P>
	 * <UL>
	 * <LI>From 2.5 to 3.0: Default value is false in 2.x versions for backward compatibility with existing
	 * implementations based on the plugin. But the <b>recommended value is true</b>.</LI>
	 * <LI>From 3.0.1: Default value is true</LI>
	 * </UL>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateDataFetcherForEveryFieldsWithArguments", defaultValue = GenerateServerCodeConfiguration.DEFAULT_GENERATE_DATA_FETCHER_FOR_EVERY_FIELD_WITH_ARGUMENT)
	public boolean generateDataFetcherForEveryFieldsWithArguments;

	/**
	 * <P>
	 * (only for server mode) Indicates whether the plugin should generate the JPA annotations, for generated objects.
	 * </P>
	 * <P>
	 * Note: if the generated code must be used with Spring 3, you must set the <i>useJakartaEE9</i> plugin parameter to
	 * <i>true</i>.
	 * </P>
	 * <P>
	 * <B><I>Default value is false</I></B>
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJPAAnnotation", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_JPA_ANNOTATION)
	boolean generateJPAAnnotation;

	/**
	 * <P>
	 * (only for server mode) Defines how the methods in the data fetchers delegates are generated. The detailed
	 * information is available in the
	 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server">Wiki server page</a>
	 * </P>
	 * <P>
	 * When generateDataLoaderForLists is false (default mode), the data loaders are used only for fields that don't
	 * return a list. In other words, for fields which type is a sub-object with an id, two methods are generated: one
	 * which returns a {@link CompletableFuture}, and one which returns a none {@link CompletableFuture} result (that is
	 * used by the generated code only if no data loader is available).
	 * </P>
	 * <P>
	 * When generateDataLoaderForLists is true, the above behavior is extended to fields that are a list.
	 * </P>
	 * <P>
	 * Note: if set to true, this plugin parameter make the use of data loader mandatory for every field which type is a
	 * list of GraphQL objects, which have an id. This may not be suitable, for instance when your data is stored in a
	 * relational database, where you would need a first query to retrieve the ids and push them into the data loader,
	 * then another one to retrieve the associated values. If you want to use data loader for only some of particular
	 * fields, you should <b>consider using the <code>generateDataLoaderForLists</code></b>. You'll find more
	 * information on the
	 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server">Wiki server
	 * page</a>.
	 * </P>
	 * <P>
	 * This parameter is available since version 1.18.4
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateDataLoaderForLists", defaultValue = GraphQLConfiguration.DEFAULT_GENERATE_DATA_LOADER_FOR_LISTS)
	boolean generateDataLoaderForLists;

	/**
	 * <P>
	 * This parameter marks a list of GraphQL mappings as ignored, so that they are not generated by the plugin. These
	 * ignored mappings can then be defined by the specific implementation.
	 * </P>
	 * <P>
	 * The other way to it is to create a spring GraphQL Controller, that overrides the controller generated by the
	 * plugin. But this may lead to this error:
	 * <code>Ambiguous mapping. Cannot map 'xxxController' method [...] to 'Type.field': there is already 'yyy' bean method [...] mapped.</code>
	 * </P>
	 * <P>
	 * The parameter may contain:
	 * </P>
	 * <UL>
	 * <LI>The '*' character: this would mark all controllers and DataFetchersDeleagate to be ignored. That is: none
	 * would be generated, and it's up to the specific implementation to manage them. In this mode, you still benefit of
	 * the POJO generation, the type wiring, the custom scalars mapping...</LI>
	 * <LI>A list of:</LI>
	 * <UL>
	 * <LI>GraphQL type name: The full controller class for this type is ignored, and won't be generated</LI>
	 * <LI>GraphQL type's field name: The method in the controller of this type, for this field, is ignored, and won't
	 * be generated. The field must be written like this: <code>{type name}.{field name}</code></LI>
	 * </UL>
	 * </UL>
	 * <P>
	 * The accepted separators for the values are: comma, space, carriage return, end of line, space, tabulation. At
	 * least one separator must exist between two values in the list. Here is a sample:
	 * </P>
	 * 
	 * <PRE>
	 *          <ignoredSpringMappings>Type1, Type2.field1
	 *          	Type3
	 *          	Type4.field2
	 *          </ignoredSpringMappings>
	 * </PRE>
	 * <P>
	 * For field mapping, there must be no separator other than '.' between the type name and the field name. For
	 * instance, the following type declaration are invalid: 'type .field', 'type. field'
	 * </P>
	 * <P>
	 * To implement the ignored mappings, you'll have to follow the [spring-graphql
	 * documentation](https://docs.spring.io/spring-graphql/reference/controllers.html).
	 * </P>
	 */
	@Parameter(property = "com.graphql_java_generator.mavenplugin.ignoredSpringMappings", defaultValue = GenerateServerCodeConfiguration.DEFAULT_IGNORED_SPRING_MAPPINGS)
	public String ignoredSpringMappings;

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
	 * look at the <A HREF=
	 * "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/usage_schema_personalization">Schema
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

	@Override
	public BatchMappingDataFetcherReturnType getBatchMappingDataFetcherReturnType() {
		return batchMappingDataFetcherReturnType;
	}

	@Override
	public String getIgnoredSpringMappings() {
		return ignoredSpringMappings;
	}

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
	public boolean isGenerateBatchLoaderEnvironment() {
		return generateBatchLoaderEnvironment;
	}

	@Override
	public boolean isGenerateBatchMappingDataFetchers() {
		return generateBatchMappingDataFetchers;
	}

	@Override
	public boolean isGenerateDataFetcherForEveryFieldsWithArguments() {
		return generateDataFetcherForEveryFieldsWithArguments;
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
