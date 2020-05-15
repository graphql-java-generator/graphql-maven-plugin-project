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
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;

/**
 * This class generates the code, from the classes coming from the com.graphql_java_generator.plugin.language package.
 * This classes have been created by {link {@link DocumentParser}
 * 
 * @author etienne-sf
 */
@Component
public class CodeGenerator {

	public static final String FILE_TYPE_JACKSON_DESERIALIZER = "Jackson deserializer";

	@Autowired
	DocumentParser documentParser;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	PluginConfiguration pluginConfiguration;

	/** The component that reads the GraphQL schema from the file system */
	@Autowired
	ResourceSchemaStringProvider resourceSchemaStringProvider;

	@Autowired
	GraphqlUtils graphqlUtils;

	/** The Velocity engine used to generate the target file */
	VelocityEngine velocityEngine = null;

	/** The context for server mode. Stored here, so that it is calculated only once */
	VelocityContext serverContext = null;

	public CodeGenerator() {
		// Initialization for Velocity
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	/**
	 * Execution of the code generation. The generation is done on the data build by the {@link #documentParser}
	 * 
	 * @Return The number of generated classes
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	public int generateCode() throws IOException {

		pluginConfiguration.getLog().debug("Starting code generation");

		int i = 0;
		pluginConfiguration.getLog().debug("Generating objects");
		i += generateTargetFiles(documentParser.getObjectTypes(), "object", resolveTemplate(CodeTemplate.OBJECT),
				false);
		pluginConfiguration.getLog().debug("Generating interfaces");
		i += generateTargetFiles(documentParser.getInterfaceTypes(), "interface",
				resolveTemplate(CodeTemplate.INTERFACE), false);
		pluginConfiguration.getLog().debug("Generating unions");
		i += generateTargetFiles(documentParser.getUnionTypes(), "union", resolveTemplate(CodeTemplate.UNION), false);
		pluginConfiguration.getLog().debug("Generating enums");
		i += generateTargetFiles(documentParser.getEnumTypes(), "enum", resolveTemplate(CodeTemplate.ENUM), false);

		switch (pluginConfiguration.getMode()) {
		case server:
			pluginConfiguration.getLog().debug("Starting server specific code generation");
			i += generateServerFiles();
			break;
		case client:
			pluginConfiguration.getLog().debug("Starting client specific code generation");

			// Generation of the query/mutation/subscription classes
			pluginConfiguration.getLog().debug("Generating query");
			i += generateTargetFiles(documentParser.getQueryTypes(), "query",
					resolveTemplate(CodeTemplate.QUERY_MUTATION), true);
			pluginConfiguration.getLog().debug("Generating mutation");
			i += generateTargetFiles(documentParser.getMutationTypes(), "mutation",
					resolveTemplate(CodeTemplate.QUERY_MUTATION), true);
			pluginConfiguration.getLog().debug("Generating subscription");
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "subscription",
					resolveTemplate(CodeTemplate.SUBSCRIPTION), true);

			// Generation of the query/mutation/subscription executor classes
			pluginConfiguration.getLog().debug("Generating query executors");
			i += generateTargetFiles(documentParser.getQueryTypes(), "executor",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_EXECUTOR), true);
			pluginConfiguration.getLog().debug("Generating mutation executors");
			i += generateTargetFiles(documentParser.getMutationTypes(), "executor",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_EXECUTOR), true);
			pluginConfiguration.getLog().debug("Generating subscription executors");
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "executor",
					resolveTemplate(CodeTemplate.SUBSCRIPTION_EXECUTOR), true);

			// Generation of the query/mutation/subscription response classes
			if (pluginConfiguration.isGenerateDeprecatedRequestResponse()) {
				pluginConfiguration.getLog().debug("Generating query response");
				i += generateTargetFiles(documentParser.getQueryTypes(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
				pluginConfiguration.getLog().debug("Generating mutation response");
				i += generateTargetFiles(documentParser.getMutationTypes(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
				pluginConfiguration.getLog().debug("Generating subscription response");
				i += generateTargetFiles(documentParser.getSubscriptionTypes(), "response",
						resolveTemplate(CodeTemplate.QUERY_RESPONSE), true);
			}

			// Generation of the query/mutation/subscription root responses classes
			pluginConfiguration.getLog().debug("Generating query root response");
			i += generateTargetFiles(documentParser.getQueryTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);
			pluginConfiguration.getLog().debug("Generating mutation root response");
			i += generateTargetFiles(documentParser.getMutationTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);
			pluginConfiguration.getLog().debug("Generating subscription root response");
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE), true);

			// Generation of the GraphQLRequest class
			pluginConfiguration.getLog().debug("Generating GraphQL Request class");
			i += generateGraphQLRequest();

			// Files for Custom Scalars
			pluginConfiguration.getLog().debug("Generating CustomScalarRegistryInitializer");
			i += generateOneFile(getJavaFile("CustomScalarRegistryInitializer", true),
					"Generating CustomScalarRegistryInitializer", getVelocityContext(),
					resolveTemplate(CodeTemplate.CUSTOM_SCALAR_REGISTRY_INITIALIZER));
			// Files for Directives
			pluginConfiguration.getLog().debug("Generating DirectiveRegistryInitializer");
			i += generateOneFile(getJavaFile("DirectiveRegistryInitializer", true),
					"Generating DirectiveRegistryInitializer", getVelocityContext(),
					resolveTemplate(CodeTemplate.DIRECTIVE_REGISTRY_INITIALIZER));
			//
			pluginConfiguration.getLog().debug("Generating " + FILE_TYPE_JACKSON_DESERIALIZER);
			i += generateTargetFiles(documentParser.customScalars, FILE_TYPE_JACKSON_DESERIALIZER,
					resolveTemplate(CodeTemplate.JACKSON_DESERIALIZER), true);

			//
			// Now useless? (to be confirmed after the first successful full build)
			// pluginConfiguration.getLog().debug("Generating query target files");
			// i += generateQueryTargetType();

			break;
		}

		if (pluginConfiguration.isCopyRuntimeSources()) {
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
						&& pluginConfiguration.getMode().equals(PluginMode.server);
				boolean clientAndIsServerFile = entry.getName().contains("com/graphql_java_generator/server")
						&& pluginConfiguration.getMode().equals(PluginMode.client);

				if (!metaInf && !serverAndIsClientFile & !clientAndIsServerFile) {
					java.io.File file = new java.io.File(pluginConfiguration.getTargetSourceFolder(), entry.getName());

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
	 * @param object
	 *            The object to send to the template
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
		int i = 0;
		for (Type object : objects) {
			String targetFileName = (String) execWithOneStringParam("getTargetFileName", object, type);
			File targetFile = getJavaFile(targetFileName, utilityClass);
			String msg = "Generating " + type + " '" + object.getName() + "' into " + targetFile.getAbsolutePath();

			VelocityContext context = getVelocityContext();
			context.put("object", object);
			context.put("targetFileName", targetFileName);
			context.put("type", type);

			i += generateOneFile(targetFile, msg, context, templateFilename);
		} // for
		return i;
	}

	/**
	 * Generates the GraphQLRequest class . This method expects at most one query, one mutation and one subscription,
	 * which is compliant with the GraphQL specification
	 */
	int generateGraphQLRequest() {
		VelocityContext context = getVelocityContext();

		context.put("query", (documentParser.queryTypes.size() > 0) ? documentParser.queryTypes.get(0) : null);
		context.put("mutation", (documentParser.mutationTypes.size() > 0) ? documentParser.mutationTypes.get(0) : null);
		context.put("subscription",
				(documentParser.subscriptionTypes.size() > 0) ? documentParser.subscriptionTypes.get(0) : null);

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

		pluginConfiguration.getLog().debug("Generating GraphQLServerMain");
		ret += generateOneFile(getJavaFile("GraphQLServerMain", true), "generating GraphQLServerMain", context,
				resolveTemplate(CodeTemplate.SERVER));

		pluginConfiguration.getLog().debug("Generating GraphQLProvider");
		ret += generateOneFile(getJavaFile("GraphQLProvider", true), "generating GraphQLProvider", context,
				resolveTemplate(CodeTemplate.PROVIDER));

		pluginConfiguration.getLog().debug("Generating GraphQLDataFetchers");
		ret += generateOneFile(getJavaFile("GraphQLDataFetchers", true), "generating GraphQLDataFetchers", context,
				resolveTemplate(CodeTemplate.DATA_FETCHER));

		for (DataFetchersDelegate dataFetcherDelegate : documentParser.dataFetchersDelegates) {
			context.put("dataFetcherDelegate", dataFetcherDelegate);
			pluginConfiguration.getLog().debug("Generating " + dataFetcherDelegate.getPascalCaseName());
			ret += generateOneFile(getJavaFile(dataFetcherDelegate.getPascalCaseName(), true),
					"generating " + dataFetcherDelegate.getPascalCaseName(), context,
					resolveTemplate(CodeTemplate.DATA_FETCHER_DELEGATE));
		}

		pluginConfiguration.getLog().debug("Generating BatchLoaderDelegate");
		ret += generateOneFile(getJavaFile("BatchLoaderDelegate", true), "generating BatchLoaderDelegate", context,
				resolveTemplate(CodeTemplate.BATCH_LOADER_DELEGATE));

		for (BatchLoader batchLoader : documentParser.batchLoaders) {
			String name = "BatchLoaderDelegate" + batchLoader.getType().getClassSimpleName() + "Impl";
			context.put("batchLoader", batchLoader);
			pluginConfiguration.getLog().debug("Generating " + name);
			ret += generateOneFile(getJavaFile(name, true), "generating " + name, context,
					resolveTemplate(CodeTemplate.BATCH_LOADER_DELEGATE_IMPL));
		}

		pluginConfiguration.getLog().debug("Generating WebSocketConfig");
		ret += generateOneFile(getJavaFile("WebSocketConfig", true), "generating WebSocketConfig", context,
				resolveTemplate(CodeTemplate.WEB_SOCKET_CONFIG));

		pluginConfiguration.getLog().debug("Generating WebSocketHandler");
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

			pluginConfiguration.getLog().debug(msg);
			Template template = velocityEngine.getTemplate(templateFilename, "UTF-8");

			targetFile.getParentFile().mkdirs();
			if (pluginConfiguration.getSourceEncoding() != null) {
				writer = new FileWriterWithEncoding(targetFile,
						Charset.forName(pluginConfiguration.getSourceEncoding()));
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
		String packageName = (utilityClass && pluginConfiguration.isSeparateUtilityClasses())
				? documentParser.getUtilPackageName()
				: pluginConfiguration.getPackageName();
		String relativePath = packageName.replace('.', '/') + '/' + simpleClassname + ".java";
		File file = new File(pluginConfiguration.getTargetSourceFolder(), relativePath);
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
		context.put("pluginConfiguration", pluginConfiguration);
		context.put("packageUtilName", documentParser.getUtilPackageName());
		context.put("customScalars", documentParser.customScalars);
		context.put("directives", documentParser.directives);
		return context;
	}

	/**
	 * @return
	 */
	private VelocityContext getVelocityServerContext() {
		if (serverContext == null) {
			serverContext = getVelocityContext();
			serverContext.put("dataFetchersDelegates", documentParser.getDataFetchersDelegates());
			serverContext.put("interfaces", documentParser.getInterfaceTypes());
			serverContext.put("unions", documentParser.getUnionTypes());

			Set<String> imports = new ConcurrentSkipListSet<>();
			// Let's calculate the list of imports for all the GraphQL schema object, input types, interfaces and unions
			if (pluginConfiguration.isSeparateUtilityClasses()) {
				// ConcurrentSkipListSet: We need to be thread safe, for the parallel stream we use to fill it
				documentParser.types.values().parallelStream()
						.forEach(o -> imports.add(o.getPackageName() + "." + o.getClassSimpleName()));
			}
			documentParser.customScalars.parallelStream()
					.forEach(o -> imports.add(o.getPackageName() + "." + o.getClassSimpleName()));
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
		if (pluginConfiguration.getTemplates().containsKey(template.name())) {
			return pluginConfiguration.getTemplates().get(template.name());
		} else {
			return template.getDefaultValue();
		}
	}
}
