/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.conf.GenerateClientCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ScalarType;

/**
 * This class generates the code for the graphql goal/task of the plugin, from the classes coming from the
 * com.graphql_java_generator.plugin.language package. This classes have been created by {link
 * {@link GenerateCodeDocumentParser}
 * 
 * @author etienne-sf
 */
@Component
public class GenerateCodeGenerator {

	public static final String FILE_TYPE_JACKSON_DESERIALIZER = "Jackson deserializer";

	@Autowired
	GenerateCodeDocumentParser generateCodeDocumentParser;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GenerateCodeCommonConfiguration configuration;

	/** The component that reads the GraphQL schema from the file system */
	@Autowired
	ResourceSchemaStringProvider resourceSchemaStringProvider;

	@Autowired
	GraphqlUtils graphqlUtils;

	/** The Velocity engine used to generate the target file */
	VelocityEngine velocityEngine = null;

	/** The context for server mode. Stored here, so that it is calculated only once */
	VelocityContext serverContext = null;

	public GenerateCodeGenerator() {
		// Initialization for Velocity
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	/**
	 * Execution of the code generation. The generation is done on the data build by the
	 * {@link #generateCodeDocumentParser}
	 * 
	 * @Return The number of generated classes
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	public int generateCode() throws IOException {

		configuration.getLog().debug("Starting code generation");

		int i = 0;
		configuration.getLog().debug("Generating objects");
		i += generateTargetFiles(generateCodeDocumentParser.getObjectTypes(), "object",
				resolveTemplate(CodeTemplate.OBJECT), false);
		configuration.getLog().debug("Generating interfaces");
		i += generateTargetFiles(generateCodeDocumentParser.getInterfaceTypes(), "interface",
				resolveTemplate(CodeTemplate.INTERFACE), false);
		configuration.getLog().debug("Generating unions");
		i += generateTargetFiles(generateCodeDocumentParser.getUnionTypes(), "union",
				resolveTemplate(CodeTemplate.UNION), false);
		configuration.getLog().debug("Generating enums");
		i += generateTargetFiles(generateCodeDocumentParser.getEnumTypes(), "enum", resolveTemplate(CodeTemplate.ENUM),
				false);

		switch (configuration.getMode()) {
		case server:
			configuration.getLog().debug("Starting server specific code generation");
			i += generateServerFiles();
			break;
		case client:
			configuration.getLog().debug("Starting client specific code generation");

			// Generation of the query/mutation/subscription classes
			if (((GenerateClientCodeConfiguration) configuration).isGenerateDeprecatedRequestResponse()) {
				// We generate these utility classes only when asked for
				configuration.getLog().debug("Generating query");
				i += generateTargetFile(generateCodeDocumentParser.getQueryType(), "query",
						resolveTemplate(CodeTemplate.QUERY_MUTATION), true);
				configuration.getLog().debug("Generating mutation");
				i += generateTargetFile(generateCodeDocumentParser.getMutationType(), "mutation",
						resolveTemplate(CodeTemplate.QUERY_MUTATION), true);
				configuration.getLog().debug("Generating subscription");
				i += generateTargetFile(generateCodeDocumentParser.getSubscriptionType(), "subscription",
						resolveTemplate(CodeTemplate.SUBSCRIPTION), true);
			}

			// Generation of the query/mutation/subscription executor classes
			configuration.getLog().debug("Generating query executors");
			i += generateTargetFile(generateCodeDocumentParser.getQueryType(), "executor",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_EXECUTOR), true);
			configuration.getLog().debug("Generating mutation executors");
			i += generateTargetFile(generateCodeDocumentParser.getMutationType(), "executor",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_EXECUTOR), true);
			configuration.getLog().debug("Generating subscription executors");
			i += generateTargetFile(generateCodeDocumentParser.getSubscriptionType(), "executor",
					resolveTemplate(CodeTemplate.SUBSCRIPTION_EXECUTOR), true);

			// Generation of the query/mutation/subscription response classes
			if (((GenerateClientCodeConfiguration) configuration).isGenerateDeprecatedRequestResponse()) {
				configuration.getLog().debug("Generating query response");
				i += generateTargetFile(generateCodeDocumentParser.getQueryType(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
				configuration.getLog().debug("Generating mutation response");
				i += generateTargetFile(generateCodeDocumentParser.getMutationType(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
				configuration.getLog().debug("Generating subscription response");
				i += generateTargetFile(generateCodeDocumentParser.getSubscriptionType(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
			}

			// Generation of the query/mutation/subscription root responses classes
			configuration.getLog().debug("Generating query root response");
			i += generateTargetFile(generateCodeDocumentParser.getQueryType(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);
			configuration.getLog().debug("Generating mutation root response");
			i += generateTargetFile(generateCodeDocumentParser.getMutationType(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);
			configuration.getLog().debug("Generating subscription root response");
			i += generateTargetFile(generateCodeDocumentParser.getSubscriptionType(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);

			// Generation of the GraphQLRequest class
			configuration.getLog().debug("Generating GraphQL Request class");
			i += generateGraphQLRequest();

			// Files for Custom Scalars
			configuration.getLog().debug("Generating CustomScalarRegistryInitializer");
			i += generateOneFile(getJavaFile("CustomScalarRegistryInitializer", true),
					"Generating CustomScalarRegistryInitializer", getVelocityContext(),
					resolveTemplate(CodeTemplate.CUSTOM_SCALAR_REGISTRY_INITIALIZER));
			// Files for Directives
			configuration.getLog().debug("Generating DirectiveRegistryInitializer");
			i += generateOneFile(getJavaFile("DirectiveRegistryInitializer", true),
					"Generating DirectiveRegistryInitializer", getVelocityContext(),
					resolveTemplate(CodeTemplate.DIRECTIVE_REGISTRY_INITIALIZER));
			//
			configuration.getLog().debug("Generating " + FILE_TYPE_JACKSON_DESERIALIZER);
			i += generateTargetFiles(generateCodeDocumentParser.customScalars, FILE_TYPE_JACKSON_DESERIALIZER,
					resolveTemplate(CodeTemplate.JACKSON_DESERIALIZER), true);

			//
			// Now useless? (to be confirmed after the first successful full build)
			// configuration.getLog().debug("Generating query target files");
			// i += generateQueryTargetType();

			break;
		}

		if (configuration.isCopyRuntimeSources()) {
			copyRuntimeSources();
		}

		return i;

	}

	void copyRuntimeSources() throws IOException {
		final int NB_BYTES = 1000;
		ClassPathResource res = new ClassPathResource("/graphql-java-runtime-sources.jar");
		JarEntry entry;
		int nbBytesRead;
		byte[] bytes = new byte[NB_BYTES];

		try (JarInputStream jar = new JarInputStream(res.getInputStream())) {
			while ((entry = jar.getNextJarEntry()) != null) {
				boolean metaInf = entry.getName().startsWith("META-INF");
				boolean serverAndIsClientFile = entry.getName().contains("com/graphql_java_generator/client")
						&& configuration.getMode().equals(PluginMode.server);
				boolean clientAndIsServerFile = entry.getName().contains("com/graphql_java_generator/server")
						&& configuration.getMode().equals(PluginMode.client);

				if (!metaInf && !serverAndIsClientFile & !clientAndIsServerFile) {
					java.io.File file = new java.io.File(configuration.getTargetSourceFolder(), entry.getName());

					if (entry.isDirectory()) {
						// if its a directory, create it
						file.mkdir();
					} else {
						try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
							while ((nbBytesRead = jar.read(bytes, 0, bytes.length)) > 0) {
								fos.write(bytes, 0, nbBytesRead);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Utility method to centralize the common actions around the file generation.
	 * 
	 * @param objects
	 *            The list of objects to send to the template
	 * @param type
	 *            The kind of graphql object (object, query, mutation...), just for proper logging
	 * @param templateFilename
	 *            The absolute path for the template (or relative to the current path)
	 * @param utilityClass
	 *            true if this class is a utility class, false if it is not. A utility class it a class the is not
	 *            directly the transposition of an item in the GraphQL schema (like object, interface, union, query...)
	 * @throws IOException
	 */
	int generateTargetFiles(List<? extends Type> objects, String type, String templateFilename, boolean utilityClass)
			throws RuntimeException {
		int ret = 0;
		for (Type object : objects) {
			ret += generateTargetFile(object, type, templateFilename, utilityClass);
		}
		return ret;
	}

	/**
	 * Utility method to centralize the common actions around the file generation.
	 * 
	 * @param object
	 *            The object to send to the template. It can be null, in which case the method doesn't do anything, and
	 *            just return 0.
	 * @param type
	 *            The kind of graphql object (object, query, mutation...), just for proper logging
	 * @param templateFilename
	 *            The absolute path for the template (or relative to the current path)
	 * @param utilityClass
	 *            true if this class is a utility class, false if it is not. A utility class it a class the is not
	 *            directly the transposition of an item in the GraphQL schema (like object, interface, union, query...)
	 * @return 1 if one file was generated, or 0 if object is null.
	 */
	int generateTargetFile(Type object, String type, String templateFilename, boolean utilityClass) {
		if (object == null) {
			return 0;
		} else {
			String targetFileName = (String) execWithOneStringParam("getTargetFileName", object, type);
			File targetFile = getJavaFile(targetFileName, utilityClass);
			String msg = "Generating " + type + " '" + object.getName() + "' into " + targetFile.getAbsolutePath();

			VelocityContext context = getVelocityContext();
			context.put("object", object);
			context.put("targetFileName", targetFileName);
			context.put("type", type);

			generateOneFile(targetFile, msg, context, templateFilename);
			return 1;
		}
	}

	/**
	 * Generates the GraphQLRequest class . This method expects at most one query, one mutation and one subscription,
	 * which is compliant with the GraphQL specification
	 */
	int generateGraphQLRequest() {
		VelocityContext context = getVelocityContext();

		context.put("query", generateCodeDocumentParser.getQueryType());
		context.put("mutation", generateCodeDocumentParser.getMutationType());
		context.put("subscription", generateCodeDocumentParser.getSubscriptionType());

		return generateOneFile(getJavaFile("GraphQLRequest", true), "generating GraphQLRequest", context,
				resolveTemplate(CodeTemplate.GRAPHQL_REQUEST));
	}

	/**
	 * Generates the server classes
	 * 
	 * @return The number of classes created, that is: 1
	 * @throws IOException
	 */
	int generateServerFiles() throws IOException {

		VelocityContext context = getVelocityServerContext();

		// List of found schemas
		List<String> schemaFiles = new ArrayList<>();
		for (org.springframework.core.io.Resource res : resourceSchemaStringProvider.schemas()) {
			schemaFiles.add(res.getFilename());
		}
		context.put("schemaFiles", schemaFiles);

		int ret = 0;

		configuration.getLog().debug("Generating GraphQLServerMain");
		ret += generateOneFile(getJavaFile("GraphQLServerMain", true), "generating GraphQLServerMain", context,
				resolveTemplate(CodeTemplate.SERVER));

		configuration.getLog().debug("Generating GraphQLProvider");
		ret += generateOneFile(getJavaFile("GraphQLProvider", true), "generating GraphQLProvider", context,
				resolveTemplate(CodeTemplate.PROVIDER));

		configuration.getLog().debug("Generating GraphQLDataFetchers");
		ret += generateOneFile(getJavaFile("GraphQLDataFetchers", true), "generating GraphQLDataFetchers", context,
				resolveTemplate(CodeTemplate.DATA_FETCHER));

		for (DataFetchersDelegate dataFetcherDelegate : generateCodeDocumentParser.dataFetchersDelegates) {
			context.put("dataFetcherDelegate", dataFetcherDelegate);
			configuration.getLog().debug("Generating " + dataFetcherDelegate.getPascalCaseName());
			ret += generateOneFile(getJavaFile(dataFetcherDelegate.getPascalCaseName(), true),
					"generating " + dataFetcherDelegate.getPascalCaseName(), context,
					resolveTemplate(CodeTemplate.DATA_FETCHER_DELEGATE));
		}

		for (BatchLoader batchLoader : generateCodeDocumentParser.batchLoaders) {
			String name = "BatchLoaderDelegate" + batchLoader.getType().getClassSimpleName() + "Impl";
			context.put("batchLoader", batchLoader);
			configuration.getLog().debug("Generating " + name);
			ret += generateOneFile(getJavaFile(name, true), "generating " + name, context,
					resolveTemplate(CodeTemplate.BATCH_LOADER_DELEGATE_IMPL));
		}

		configuration.getLog().debug("Generating WebSocketConfig");
		ret += generateOneFile(getJavaFile("WebSocketConfig", true), "generating WebSocketConfig", context,
				resolveTemplate(CodeTemplate.WEB_SOCKET_CONFIG));

		configuration.getLog().debug("Generating WebSocketHandler");
		ret += generateOneFile(getJavaFile("WebSocketHandler", true), "generating WebSocketHandler", context,
				resolveTemplate(CodeTemplate.WEB_SOCKET_HANDLER));

		return ret;

	}

	/**
	 * Generates one file from the given parameter, based on a Velocity context and template.
	 * 
	 * @param targetFile
	 *            The file to be generated. It will be either created or rewrited
	 * @param msg
	 *            A log message. It will be logged in debug mode, or send as the error message if an exception is
	 *            raised.
	 * @param context
	 *            The Velocity context
	 * @param templateFilename
	 *            The Velocity template
	 * @return The number of classes created, that is: 1
	 */
	int generateOneFile(File targetFile, String msg, VelocityContext context, String templateFilename) {
		try {
			Writer writer = null;

			configuration.getLog().debug(msg);
			Template template = velocityEngine.getTemplate(templateFilename, "UTF-8");

			targetFile.getParentFile().mkdirs();
			if (configuration.getSourceEncoding() != null) {
				writer = new FileWriterWithEncoding(targetFile, Charset.forName(configuration.getSourceEncoding()));
			} else {
				writer = new FileWriter(targetFile);
			}
			template.merge(context, writer);
			writer.flush();
			writer.close();

			// Let's return the number of created files. That is: 1.
			// Not very useful. But it helps making simpler the code of the caller for this
			// method
			return 1;
		} catch (ResourceNotFoundException | ParseErrorException | TemplateInitException | MethodInvocationException
				| IOException e) {
			throw new RuntimeException("Error when " + msg, e);
		}
	}

	/**
	 * This method returns the {@link File} where the class is to be generated. It adds the preceding path, and the
	 * suffix ".java" to the given parameter.
	 * 
	 * @param simpleClassname
	 *            The classname, without the package
	 * @param utilityClass
	 *            true if this class is a utility class, false if it is not. A utility class it a class the is not
	 *            directly the transposition of an item in the GraphQL schema (like object, interface, union, query...)
	 * @return
	 */
	File getJavaFile(String simpleClassname, boolean utilityClass) {
		String packageName = (utilityClass && configuration.isSeparateUtilityClasses())
				? generateCodeDocumentParser.getUtilPackageName()
				: configuration.getPackageName();
		String relativePath = packageName.replace('.', '/') + '/' + simpleClassname + ".java";
		File file = new File(configuration.getTargetSourceFolder(), relativePath);
		file.getParentFile().mkdirs();
		return file;
	}

	/**
	 * Calls the 'methodName' method on the given object
	 * 
	 * @param methodName
	 *            The name of the method name
	 * @param object
	 *            The given node, on which the 'methodName' method is to be called
	 * @return
	 */
	Object exec(String methodName, Object object) {
		try {
			Method getType = object.getClass().getMethod(methodName);
			return getType.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' on '"
					+ object.getClass().getName() + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Calls the 'methodName' method on the given object
	 * 
	 * @param methodName
	 *            The name of the method name
	 * @param object
	 *            The given node, on which the 'methodName' method is to be called
	 * @return
	 */
	Object execWithOneStringParam(String methodName, Object object, String param) {
		try {
			Method getType = object.getClass().getMethod(methodName, String.class);
			return getType.invoke(object, param);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' (with a String param) on '"
					+ object.getClass().getName() + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Returns a {@link VelocityContext} with all default values filled.
	 * 
	 * @return
	 */
	VelocityContext getVelocityContext() {
		VelocityContext context = new VelocityContext();
		context.put("configuration", configuration);
		// Velocity can't access to enum values. So we add it into the context
		context.put("isPluginModeClient", configuration.getMode() == PluginMode.client);

		context.put("packageUtilName", generateCodeDocumentParser.getUtilPackageName());
		context.put("customScalars", generateCodeDocumentParser.customScalars);
		context.put("directives", generateCodeDocumentParser.directives);
		return context;
	}

	/**
	 * @return
	 */
	private VelocityContext getVelocityServerContext() {
		if (serverContext == null) {
			serverContext = getVelocityContext();
			serverContext.put("dataFetchersDelegates", generateCodeDocumentParser.getDataFetchersDelegates());
			serverContext.put("interfaces", generateCodeDocumentParser.getInterfaceTypes());
			serverContext.put("unions", generateCodeDocumentParser.getUnionTypes());

			// ConcurrentSkipListSet: We need to be thread safe, for the parallel stream we use to fill it
			Set<String> imports = new ConcurrentSkipListSet<>();
			// Let's calculate the list of imports of all the GraphQL schema object, input types, interfaces and unions,
			// that must be imported in the utility classes
			final String utilityPackage = configuration.getPackageName()
					+ ((configuration.isSeparateUtilityClasses()) ? ("." + GenerateCodeDocumentParser.UTIL_PACKAGE_NAME)
							: "");
			generateCodeDocumentParser.types.values().parallelStream().forEach(o -> {
				if (o instanceof ScalarType) {
					graphqlUtils.addImport(imports, utilityPackage,
							((ScalarType) o).getPackageName() + "." + ((ScalarType) o).getClassSimpleName());
				} else {
					graphqlUtils.addImport(imports, utilityPackage,
							configuration.getPackageName() + "." + o.getClassSimpleName());
				}
			});
			serverContext.put("imports", imports);
		}
		return serverContext;
	}

	/**
	 * Resolves the template for the given key
	 * 
	 * @param templateKey
	 * @param defaultValue
	 * @return
	 */
	protected String resolveTemplate(CodeTemplate template) {
		if (configuration.getTemplates().containsKey(template.name())) {
			return configuration.getTemplates().get(template.name());
		} else {
			return template.getDefaultValue();
		}
	}
}
