/**
 * 
 */
package com.graphql_java_generator.plugin.generate_code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.Generator;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.GenerateClientCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.conf.GeneratePojoConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.generate_schema.GenerateGraphQLSchema;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.Type.TargetFileType;
import com.graphql_java_generator.util.GraphqlUtils;
import com.graphql_java_generator.util.VelocityUtils;

/**
 * This class generates the code for the graphql goals/tasks of the plugin, from the classes coming from the
 * com.graphql_java_generator.plugin.language package. This classes have been created by {link
 * {@link GenerateCodeDocumentParser}.<BR/>
 * This class should not be used directly. Please use the {@link GenerateCodePluginExecutor} instead.
 * 
 * @author etienne-sf
 */
@Component
public class GenerateCodeGenerator implements Generator, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(GenerateCodeGenerator.class);

	private final static String COMMON_RUNTIME_SOURCE_FILENAME = "/graphql-java-common-runtime-sources.jar"; //$NON-NLS-1$
	private final static String CLIENT_RUNTIME_SOURCE_FILENAME = "/graphql-java-client-runtime-sources.jar"; //$NON-NLS-1$
	private final static String SERVER_RUNTIME_SOURCE_FILENAME = "/graphql-java-server-runtime-sources.jar"; //$NON-NLS-1$

	private final static String SPRING_AUTO_CONFIGURATION_CLASS = "GraphQLPluginAutoConfiguration"; //$NON-NLS-1$

	@Autowired
	ApplicationContext ctx;

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

	/** The Velocity engine, that will merge the templates with their context */
	VelocityEngine velocityEngine = null;

	/** The context for server mode. Stored here, so that it is calculated only once */
	VelocityContext serverContext = null;

	@Override
	public void afterPropertiesSet() {
		// Initialization for Velocity
		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath,file,str"); //$NON-NLS-1$

		// Configuration for 'real' executions of the plugin (that is: from the plugin's packaged jar)
		this.velocityEngine.setProperty("resource.loader.classpath.description", "Velocity Classpath Resource Loader"); //$NON-NLS-1$ //$NON-NLS-2$
		this.velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName()); //$NON-NLS-1$

		// Used for a workaround of a gradle issue: Velocity doesn't seem to properly find templates that are in another
		// jar. See the generateOneFile() method for the usage
		this.velocityEngine.setProperty("resource.loader.str.description", "Velocity String Resource Loader"); //$NON-NLS-1$ //$NON-NLS-2$
		this.velocityEngine.setProperty("resource.loader.str.class", StringResourceLoader.class.getName()); //$NON-NLS-1$

		// Configuration for the unit tests (that is: from the file system)
		this.velocityEngine.setProperty("resource.loader.file.description", "Velocity File Resource Loader"); //$NON-NLS-1$ //$NON-NLS-2$
		this.velocityEngine.setProperty("resource.loader.file.class", FileResourceLoader.class.getName()); //$NON-NLS-1$
		this.velocityEngine.setProperty("resource.loader.file.path", //$NON-NLS-1$
				this.configuration.getProjectDir().getAbsolutePath());
		this.velocityEngine.setProperty("resource.loader.file.cache", Boolean.TRUE); //$NON-NLS-1$

		this.velocityEngine.init();
	}

	/**
	 * Execution of the code generation. The generation is done on the data build by the
	 * {@link #generateCodeDocumentParser}
	 * 
	 * @Return The number of generated classes
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public int generateCode() throws IOException {

		logger.debug("Starting code generation"); //$NON-NLS-1$

		int i = 0;
		logger.debug("Generating objects"); //$NON-NLS-1$
		i += generateTargetFilesForTypeList(this.generateCodeDocumentParser.getObjectTypes(), TargetFileType.OBJECT,
				CodeTemplate.OBJECT, false);
		logger.debug("Generating interfaces"); //$NON-NLS-1$
		i += generateTargetFilesForTypeList(this.generateCodeDocumentParser.getInterfaceTypes(),
				TargetFileType.INTERFACE, CodeTemplate.INTERFACE, false);
		logger.debug("Generating unions"); //$NON-NLS-1$
		i += generateTargetFilesForTypeList(this.generateCodeDocumentParser.getUnionTypes(), TargetFileType.UNION,
				CodeTemplate.UNION, false);
		logger.debug("Generating enums"); //$NON-NLS-1$
		i += generateTargetFilesForTypeList(this.generateCodeDocumentParser.getEnumTypes(), TargetFileType.ENUM,
				CodeTemplate.ENUM, false);

		switch (this.configuration.getMode()) {
		case server:
			i += generateServerFiles();
			break;
		case client:
			i += generateClientFiles();
			break;
		}// switch (configuration.getMode())

		if (this.configuration.isCopyRuntimeSources()) {
			copyRuntimeSources();
		} else if (this.configuration instanceof GeneratePojoConfiguration) {
			logger.info("You're using the generatePojo goal/task with copyRuntimeSources set to false. " //$NON-NLS-1$
					+ "To avoid adding plugin dependencies, the recommended value for the plugin parameter 'copyRuntimeSources' is true. " //$NON-NLS-1$
					+ "Please note that the default value changed from true to false since 2.0."); //$NON-NLS-1$
		}
		logger.info(
				i + " java classes have been generated from the schema(s) '" + this.configuration.getSchemaFilePattern() //$NON-NLS-1$
						+ "' in the package '" + this.configuration.getPackageName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return i;
	}

	private int generateClientFiles() throws IOException {
		int i = 0;
		logger.debug("Starting client specific code generation"); //$NON-NLS-1$

		// Custom Deserializers and array deserialization (always generated)
		VelocityContext context = getVelocityContext();
		context.put("customDeserializers", this.generateCodeDocumentParser.getCustomDeserializers()); //$NON-NLS-1$
		context.put("customSerializers", this.generateCodeDocumentParser.getCustomSerializers()); //$NON-NLS-1$

		if (this.configuration.isGenerateJacksonAnnotations()) {
			i += generateOneJavaFile("CustomJacksonDeserializers", true, "Generating custom deserializers", context, //$NON-NLS-1$ //$NON-NLS-2$
					CodeTemplate.JACKSON_DESERIALIZERS);
			i += generateOneJavaFile("CustomJacksonSerializers", true, "Generating custom serializers", context, //$NON-NLS-1$ //$NON-NLS-2$
					CodeTemplate.JACKSON_SERIALIZERS);
		}

		if (this.configuration.isGenerateUtilityClasses()) {

			// Generation of the query/mutation/subscription classes
			if (((GenerateClientCodeConfiguration) this.configuration).isGenerateDeprecatedRequestResponse()) {
				// We generate these utility classes only when asked for
				logger.debug("Generating query"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getQueryType(), TargetFileType.QUERY,
						CodeTemplate.QUERY_MUTATION, true);
				logger.debug("Generating mutation"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getMutationType(),
						TargetFileType.MUTATION, CodeTemplate.QUERY_MUTATION, true);
				logger.debug("Generating subscription"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getSubscriptionType(),
						TargetFileType.SUBSCRIPTION, CodeTemplate.SUBSCRIPTION, true);
			}

			// Generation of the query/mutation/subscription executor classes
			logger.debug("Generating query executor"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getQueryType(), TargetFileType.EXECUTOR,
					CodeTemplate.QUERY_MUTATION_EXECUTOR, true);
			logger.debug("Generating query reactive executor"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getQueryType(),
					TargetFileType.REACTIVE_EXECUTOR, CodeTemplate.QUERY_MUTATION_REACTIVE_EXECUTOR, true);
			logger.debug("Generating mutation executor"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getMutationType(), TargetFileType.EXECUTOR,
					CodeTemplate.QUERY_MUTATION_EXECUTOR, true);
			logger.debug("Generating mutation reactive executor"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getMutationType(),
					TargetFileType.REACTIVE_EXECUTOR, CodeTemplate.QUERY_MUTATION_REACTIVE_EXECUTOR, true);
			logger.debug("Generating subscription executor"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getSubscriptionType(),
					TargetFileType.EXECUTOR, CodeTemplate.SUBSCRIPTION_EXECUTOR, true);

			// Generation of the query/mutation/subscription response classes
			if (((GenerateClientCodeConfiguration) this.configuration).isGenerateDeprecatedRequestResponse()) {
				logger.debug("Generating query response"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getQueryType(), TargetFileType.RESPONSE,
						CodeTemplate.QUERY_RESPONSE, true);
				logger.debug("Generating mutation response"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getMutationType(),
						TargetFileType.RESPONSE, CodeTemplate.QUERY_RESPONSE, true);
				logger.debug("Generating subscription response"); //$NON-NLS-1$
				i += generateTargetFileForType(this.generateCodeDocumentParser.getSubscriptionType(),
						TargetFileType.RESPONSE, CodeTemplate.QUERY_RESPONSE, true);
			}

			// Generation of the query/mutation/subscription root responses classes
			logger.debug("Generating query root response"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getQueryType(), TargetFileType.ROOT_RESPONSE,
					CodeTemplate.ROOT_RESPONSE, true);
			logger.debug("Generating mutation root response"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getMutationType(),
					TargetFileType.ROOT_RESPONSE, CodeTemplate.ROOT_RESPONSE, true);
			logger.debug("Generating subscription root response"); //$NON-NLS-1$
			i += generateTargetFileForType(this.generateCodeDocumentParser.getSubscriptionType(),
					TargetFileType.ROOT_RESPONSE, CodeTemplate.ROOT_RESPONSE, true);

			// Generation of the GraphQLRequest class
			logger.debug("Generating GraphQL Request class"); //$NON-NLS-1$
			i += generateGraphQLRequest();

			// Files for Custom Scalars
			logger.debug("Generating CustomScalarRegistryInitializer"); //$NON-NLS-1$
			i += generateOneJavaFile("CustomScalarRegistryInitializer", true, //$NON-NLS-1$
					"Generating CustomScalarRegistryInitializer", getVelocityContext(), //$NON-NLS-1$
					CodeTemplate.CUSTOM_SCALAR_REGISTRY_INITIALIZER);

			// Files for Directives
			logger.debug("Generating DirectiveRegistryInitializer"); //$NON-NLS-1$
			i += generateOneJavaFile("DirectiveRegistryInitializer", true, "Generating DirectiveRegistryInitializer", //$NON-NLS-1$ //$NON-NLS-2$
					getVelocityContext(), CodeTemplate.DIRECTIVE_REGISTRY_INITIALIZER);

			// Generation of the Spring Configuration class, that is specific to this GraphQL schema
			logger.debug("Generating Spring autoconfiguration class"); //$NON-NLS-1$
			i += generateOneJavaFile(
					SPRING_AUTO_CONFIGURATION_CLASS + (this.configuration.getSpringBeanSuffix() == null ? "" //$NON-NLS-1$
							: this.configuration.getSpringBeanSuffix()),
					true, "generating SpringConfiguration", context, //$NON-NLS-1$
					CodeTemplate.CLIENT_SPRING_AUTO_CONFIGURATION_CLASS);

			// Spring auto-configuration management
			logger.debug("Generating Spring autoconfiguration generation"); //$NON-NLS-1$
			String autoConfClass = this.configuration.getSpringAutoConfigurationPackage() + "." //$NON-NLS-1$
					+ SPRING_AUTO_CONFIGURATION_CLASS + (this.configuration.getSpringBeanSuffix() == null ? "" //$NON-NLS-1$
							: this.configuration.getSpringBeanSuffix());
			i += generateSpringAutoConfigurationDeclaration(autoConfClass);
		}

		logger.debug("Generating client side mapping from graphql type to java type"); //$NON-NLS-1$
		i += generateClientTypeMapping();

		return i;
	}

	/**
	 * Generate a class for mapping from graphql types to client java classes.
	 *
	 * @return
	 */
	private int generateClientTypeMapping() {
		VelocityContext context = getVelocityContext();
		context.put("types", this.generateCodeDocumentParser.getTypes()); //$NON-NLS-1$

		generateOneJavaFile("GraphQLTypeMapping", false, "generating GraphQLTypeMapping", context, //$NON-NLS-1$ //$NON-NLS-2$
				CodeTemplate.TYPE_MAPPING);

		return 1;
	}

	/**
	 * Generates the Spring auto-configuration file (META-INF/spring.factories), or update it to declare the
	 * SpringAutoConfiguration for this generation
	 * 
	 * @return
	 * @throws IOException
	 */
	private int generateSpringAutoConfigurationDeclaration(String autoConfClass) throws IOException {
		String springAutoConfigurationPath = "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"; //$NON-NLS-1$
		File springFactories = new File(this.configuration.getTargetResourceFolder(), springAutoConfigurationPath);
		Set<String> autoConfClasses = new TreeSet<>();
		autoConfClasses.add(autoConfClass);
		if (springFactories.exists()) {
			String line;
			springFactories.getParentFile().mkdirs();
			try (BufferedReader br = new BufferedReader(new FileReader(springFactories))) {
				while ((line = br.readLine()) != null) {
					if (line.equals(autoConfClass)) {
						// The auto configuration class is already defined there. There is nothing to do
						return 0;
					}
					autoConfClasses.add(line);
				}
			}
		}

		VelocityContext context = getVelocityContext();
		context.put("springAutoConfigurationClasses", String.join("\n", autoConfClasses)); //$NON-NLS-1$ //$NON-NLS-2$
		generateOneFile(getResourceFile(springAutoConfigurationPath), "Generating " + springAutoConfigurationPath, //$NON-NLS-1$
				context, CodeTemplate.SPRING_AUTOCONFIGURATION_DEFINITION_FILE);

		return 1;
	}

	void copyRuntimeSources() throws IOException {
		final int NB_BYTES = 1000;
		JarEntry entry;
		java.io.File file;
		String targetFilename;
		int nbBytesRead;
		byte[] bytes = new byte[NB_BYTES];

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the common runtime
		try (JarInputStream jar = new JarInputStream(
				new ClassPathResource(COMMON_RUNTIME_SOURCE_FILENAME).getInputStream())) {
			while ((entry = jar.getNextJarEntry()) != null) {

				// Folders are ignored here.
				if (entry.isDirectory())
					continue;

				// We skip the /META-INF/ folder that just contains the MANIFEST file
				if (entry.getName().startsWith("META-INF") || entry.getName().equals("java/") //$NON-NLS-1$ //$NON-NLS-2$
						|| entry.getName().equals("resources/")) { //$NON-NLS-1$
					continue;
				}
				if (!entry.getName().startsWith("resources") && !entry.getName().startsWith("java")) { //$NON-NLS-1$ //$NON-NLS-2$
					throw new RuntimeException("The entries in the '" + COMMON_RUNTIME_SOURCE_FILENAME //$NON-NLS-1$
							+ "' file should start either by 'java' or by 'resources', but this entry doesn't: " //$NON-NLS-1$
							+ entry.getName());
				}

				targetFilename = entry.getName().substring("java".length() + 1); //$NON-NLS-1$

				boolean copyFile = true;// Default is to copy the file
				if (this.configuration instanceof GeneratePojoConfiguration) {
					// if the goal/task is generatePojo, then only part of the dependencies should be copied.
					copyFile = targetFilename.startsWith("com/graphql_java_generator/annotation"); //$NON-NLS-1$
				}

				if (copyFile) {
					file = new java.io.File(this.configuration.getTargetSourceFolder(), targetFilename);

					file.getParentFile().mkdirs();
					try (OutputStream fos = new FileOutputStream(file)) {
						while ((nbBytesRead = jar.read(bytes, 0, bytes.length)) > 0) {
							fos.write(bytes, 0, nbBytesRead);
						}
					}
				}
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step 2 : the client runtime
		if (this.configuration.getMode().equals(PluginMode.client)) {
			try (JarInputStream jar = new JarInputStream(
					new ClassPathResource(CLIENT_RUNTIME_SOURCE_FILENAME).getInputStream())) {
				while ((entry = jar.getNextJarEntry()) != null) {

					// Folders are ignored here.
					if (entry.isDirectory())
						continue;

					// We skip the /META-INF/ folder that just contains the MANIFEST file
					if (entry.getName().startsWith("META-INF") || entry.getName().equals("java/") //$NON-NLS-1$ //$NON-NLS-2$
							|| entry.getName().equals("resources/")) { //$NON-NLS-1$
						continue;
					}
					if (!entry.getName().startsWith("resources") && !entry.getName().startsWith("java")) { //$NON-NLS-1$ //$NON-NLS-2$
						throw new RuntimeException("The entries in the '" + CLIENT_RUNTIME_SOURCE_FILENAME //$NON-NLS-1$
								+ "' file should start either by 'java' or by 'resources', but this entry doesn't: " //$NON-NLS-1$
								+ entry.getName());
					}

					boolean resources = entry.getName().startsWith("resources"); //$NON-NLS-1$
					if (resources) {
						targetFilename = entry.getName().substring("resources".length() + 1); //$NON-NLS-1$
					} else {
						targetFilename = entry.getName().substring("java".length() + 1); //$NON-NLS-1$
					}

					boolean copyFile = true;// Default is to copy the file
					if (this.configuration instanceof GeneratePojoConfiguration) {
						// if the goal/task is generatePojo, then only part of the dependencies should be copied.
						copyFile = targetFilename.startsWith("com/graphql_java_generator/annotation") //$NON-NLS-1$
								|| (this.configuration.isGenerateJacksonAnnotations() && //
										(targetFilename
												.startsWith("com/graphql_java_generator/client/GraphQLRequestObject") //$NON-NLS-1$
												|| targetFilename.contains("AbstractCustomJacksonSerializer") //$NON-NLS-1$
												|| targetFilename.contains("AbstractCustomJacksonDeserializer"))); //$NON-NLS-1$
					}

					if (copyFile) {
						if (resources)
							file = new java.io.File(this.configuration.getTargetResourceFolder(), targetFilename);
						else
							file = new java.io.File(this.configuration.getTargetSourceFolder(), targetFilename);

						file.getParentFile().mkdirs();
						try (OutputStream fos = new FileOutputStream(file)) {
							while ((nbBytesRead = jar.read(bytes, 0, bytes.length)) > 0) {
								fos.write(bytes, 0, nbBytesRead);
							}
						}
					}
				}
			}
		} // if (mode==client)

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Step3 : the server runtime
		if (this.configuration.getMode().equals(PluginMode.server)) {
			try (JarInputStream jar = new JarInputStream(
					new ClassPathResource(SERVER_RUNTIME_SOURCE_FILENAME).getInputStream())) {
				while ((entry = jar.getNextJarEntry()) != null) {

					// Folders are ignored here.
					if (entry.isDirectory())
						continue;

					// We skip the /META-INF/ folder that just contains the MANIFEST file
					if (entry.getName().startsWith("META-INF") || entry.getName().equals("java/") //$NON-NLS-1$ //$NON-NLS-2$
							|| entry.getName().equals("resources/")) { //$NON-NLS-1$
						continue;
					}
					if (!entry.getName().startsWith("resources") && !entry.getName().startsWith("java")) { //$NON-NLS-1$ //$NON-NLS-2$
						throw new RuntimeException("The entries in the '" + SERVER_RUNTIME_SOURCE_FILENAME //$NON-NLS-1$
								+ "' file should start either by 'java' or by 'resources', but this entry doesn't: " //$NON-NLS-1$
								+ entry.getName());
					}

					targetFilename = entry.getName().substring("java".length() + 1); //$NON-NLS-1$

					boolean copyFile = true; // Default is to copy the file
					if (this.configuration instanceof GeneratePojoConfiguration) {
						// if the goal/task is generatePojo, then only part of the dependencies should be copied.
						copyFile = targetFilename.startsWith("com/graphql_java_generator/annotation") //$NON-NLS-1$
								|| (this.configuration.isGenerateJacksonAnnotations() && //
										(targetFilename
												.startsWith("com/graphql_java_generator/client/GraphQLRequestObject") //$NON-NLS-1$
												|| targetFilename.contains("AbstractCustomJacksonSerializer") //$NON-NLS-1$
												|| targetFilename.contains("AbstractCustomJacksonDeserializer"))); //$NON-NLS-1$
					}

					if (copyFile) {
						file = new java.io.File(this.configuration.getTargetSourceFolder(), targetFilename);

						file.getParentFile().mkdirs();
						try (OutputStream fos = new FileOutputStream(file)) {
							while ((nbBytesRead = jar.read(bytes, 0, bytes.length)) > 0) {
								fos.write(bytes, 0, nbBytesRead);
							}
						}
					}
				}
			}
		} // if (server mode)
	}

	/**
	 * Utility method to centralize the common actions around the file generation.
	 * 
	 * @param objects
	 *            The list of objects to send to the template
	 * @param type
	 *            The kind of graphql object (object, query, mutation...), just for proper logging
	 * @param templateCode
	 *            The absolute path for the template (or relative to the current path)
	 * @param utilityClass
	 *            true if this class is a utility class, false if it is not. A utility class it a class the is not
	 *            directly the transposition of an item in the GraphQL schema (like object, interface, union, query...)
	 * @throws IOException
	 */
	int generateTargetFilesForTypeList(List<? extends Type> objects, TargetFileType type, CodeTemplate templateCode,
			boolean utilityClass) throws RuntimeException {
		int ret = 0;
		for (Type object : objects) {
			ret += generateTargetFileForType(object, type, templateCode, utilityClass);
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
	 * @param templateCode
	 *            The template to use for this file
	 * @param utilityClass
	 *            true if this class is a utility class, false if it is not. A utility class it a class the is not
	 *            directly the transposition of an item in the GraphQL schema (like object, interface, union, query...)
	 * @return 1 if one file was generated, or 0 if object is null.
	 */
	int generateTargetFileForType(Type object, TargetFileType type, CodeTemplate templateCode, boolean utilityClass) {
		if (object == null) {
			return 0;
		} else {
			String classname = (String) execWithOneParam("getTargetFileName", object, type, TargetFileType.class); //$NON-NLS-1$
			if ((type.equals(TargetFileType.EXECUTOR) || type.equals(TargetFileType.REACTIVE_EXECUTOR))
					&& this.configuration.getSpringBeanSuffix() != null) {
				classname += this.configuration.getSpringBeanSuffix();
			}

			VelocityContext context = getVelocityContext();
			context.put("object", object); //$NON-NLS-1$
			context.put("type", type); //$NON-NLS-1$

			generateOneJavaFile(classname, utilityClass, "Generating file for " + type + " '" + object.getName() + "'", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					context, templateCode);
			return 1;
		}
	}

	/**
	 * Generates the GraphQLRequest class . This method expects at most one query, one mutation and one subscription,
	 * which is compliant with the GraphQL specification
	 */
	int generateGraphQLRequest() {
		VelocityContext context = getVelocityContext();

		context.put("query", this.generateCodeDocumentParser.getQueryType()); //$NON-NLS-1$
		context.put("mutation", this.generateCodeDocumentParser.getMutationType()); //$NON-NLS-1$
		context.put("subscription", this.generateCodeDocumentParser.getSubscriptionType()); //$NON-NLS-1$

		return generateOneJavaFile(//
				(this.configuration.getSpringBeanSuffix() == null) ? "GraphQLRequest" //$NON-NLS-1$
						: "GraphQLRequest" + this.configuration.getSpringBeanSuffix(), // //$NON-NLS-1$
				true, "generating GraphQLRequest", context, CodeTemplate.GRAPHQL_REQUEST); //$NON-NLS-1$
	}

	/**
	 * Generates the server classes
	 * 
	 * @return The number of classes created, that is: 1
	 * @throws IOException
	 */
	int generateServerFiles() throws IOException {
		int ret = 0;
		logger.debug("Starting server specific code generation"); //$NON-NLS-1$

		if (this.configuration.isGenerateUtilityClasses()) {
			logger.debug("Generating server utility classes"); //$NON-NLS-1$

			VelocityContext context = getVelocityServerContext();

			// List of found schemas
			List<String> schemaFiles = new ArrayList<>();
			for (org.springframework.core.io.Resource res : this.resourceSchemaStringProvider.schemas(false)) {
				schemaFiles.add(res.getFilename());
			}
			context.put("schemaFiles", schemaFiles); //$NON-NLS-1$

			logger.debug("Generating GraphQLServerMain"); //$NON-NLS-1$
			ret += generateOneJavaFile("GraphQLServerMain", true, "generating GraphQLServerMain", context, //$NON-NLS-1$ //$NON-NLS-2$
					CodeTemplate.SERVER);

			logger.debug("Generating GraphQLWiring"); //$NON-NLS-1$
			ret += generateOneJavaFile("GraphQLWiring", true, "generating GraphQLWiring", context, CodeTemplate.WIRING); //$NON-NLS-1$ //$NON-NLS-2$

			for (DataFetchersDelegate dataFetcherDelegate : this.generateCodeDocumentParser.dataFetchersDelegates) {
				context.put("dataFetchersDelegate", dataFetcherDelegate); //$NON-NLS-1$
				context.put("dataFetchersDelegates", this.generateCodeDocumentParser.getDataFetchersDelegates()); //$NON-NLS-1$
				context.put("batchLoaders", this.generateCodeDocumentParser.getBatchLoaders()); //$NON-NLS-1$

				String entityControllerName = this.graphqlUtils.getJavaName(dataFetcherDelegate.getType().getName())
						+ "Controller"; //$NON-NLS-1$
				logger.debug("Generating " + entityControllerName); //$NON-NLS-1$
				ret += generateOneJavaFile(entityControllerName, true, "generating " + entityControllerName, context, //$NON-NLS-1$
						CodeTemplate.ENTITY_CONTROLLER);

				logger.debug("Generating " + dataFetcherDelegate.getPascalCaseName()); //$NON-NLS-1$
				ret += generateOneJavaFile(dataFetcherDelegate.getPascalCaseName(), true,
						"generating " + dataFetcherDelegate.getPascalCaseName(), context, //$NON-NLS-1$
						CodeTemplate.DATA_FETCHER_DELEGATE);
			}

			// Generation of the Spring Configuration class, that is specific to this GraphQL schema
			logger.debug("Generating Spring autoconfiguration class"); //$NON-NLS-1$
			String className = SPRING_AUTO_CONFIGURATION_CLASS + (this.configuration.getSpringBeanSuffix() == null ? "" //$NON-NLS-1$
					: this.configuration.getSpringBeanSuffix());
			ret += generateOneJavaFile(className, true, "generating SpringConfiguration", context, //$NON-NLS-1$
					CodeTemplate.SERVER_SPRING_AUTO_CONFIGURATION_CLASS);

			// Spring auto-configuration management
			logger.debug("Generating Spring autoconfiguration generation"); //$NON-NLS-1$
			String autoConfClass = this.configuration.getSpringAutoConfigurationPackage() + "." //$NON-NLS-1$
					+ SPRING_AUTO_CONFIGURATION_CLASS + (this.configuration.getSpringBeanSuffix() == null ? "" //$NON-NLS-1$
							: this.configuration.getSpringBeanSuffix());
			ret += generateSpringAutoConfigurationDeclaration(autoConfClass);
		}

		// We're in server mode. So, When the addRelayConnections parameter is true, we must generate the resulting
		// GraphQL schema, so that the graphql-java can access it at runtime.
		// Otherwise, we just need to copy the schema file to the ./graphql folder, so that spring-graphql can find
		// them.
		if (!this.configuration.isAddRelayConnections()) {
			copySchemaFilesToGraphqlFolder();
		} else {
			GenerateGraphQLSchemaConfiguration generateGraphQLSchemaConf = new GenerateGraphQLSchemaConfiguration() {

				@Override
				public Integer getMaxTokens() {
					return GenerateCodeGenerator.this.configuration.getMaxTokens();
				}

				@Override
				public File getProjectDir() {
					return GenerateCodeGenerator.this.configuration.getProjectDir();
				}

				@Override
				public File getSchemaFileFolder() {
					return GenerateCodeGenerator.this.configuration.getSchemaFileFolder();
				}

				@Override
				public String getSchemaFilePattern() {
					return GenerateCodeGenerator.this.configuration.getSchemaFilePattern();
				}

				@Override
				public Map<String, String> getTemplates() {
					return GenerateCodeGenerator.this.configuration.getTemplates();
				}

				@Override
				public boolean isAddRelayConnections() {
					return GenerateCodeGenerator.this.configuration.isAddRelayConnections();
				}

				@Override
				public String getTypePrefix() {
					return GenerateCodeGenerator.this.configuration.getTypePrefix();
				}

				@Override
				public String getTypeSuffix() {
					return GenerateCodeGenerator.this.configuration.getTypeSuffix();
				}

				@Override
				public String getInputPrefix() {
					return GenerateCodeGenerator.this.configuration.getInputPrefix();
				}

				@Override
				public String getInputSuffix() {
					return GenerateCodeGenerator.this.configuration.getInputSuffix();
				}

				@Override
				public String getUnionPrefix() {
					return GenerateCodeGenerator.this.configuration.getUnionPrefix();
				}

				@Override
				public String getUnionSuffix() {
					return GenerateCodeGenerator.this.configuration.getUnionSuffix();
				}

				@Override
				public String getInterfacePrefix() {
					return GenerateCodeGenerator.this.configuration.getInterfacePrefix();
				}

				@Override
				public String getInterfaceSuffix() {
					return GenerateCodeGenerator.this.configuration.getInterfaceSuffix();
				}

				@Override
				public String getEnumPrefix() {
					return GenerateCodeGenerator.this.configuration.getEnumPrefix();
				}

				@Override
				public String getEnumSuffix() {
					return GenerateCodeGenerator.this.configuration.getEnumSuffix();
				}

				@Override
				public String getResourceEncoding() {
					return "UTF-8"; //$NON-NLS-1$
				}

				@Override
				public File getTargetFolder() {
					return new File(GenerateCodeGenerator.this.configuration.getTargetResourceFolder(),
							GenerateCodeGenerator.this.configuration.getTargetSchemaSubFolder());
				}

				@Override
				public String getTargetSchemaFileName() {
					return GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME;
				}

				@Override
				public String getDefaultTargetSchemaFileName() {
					return DEFAULT_TARGET_SCHEMA_FILE_NAME;
				}

				@Override
				public boolean isSkipGenerationIfSchemaHasNotChanged() {
					// If we're here, it means the the code generation is done. So the addRelayConnection schema
					// must be
					// always created. We won't skip it.
					return false;
				}
			};
			GenerateGraphQLSchema generateGraphQLSchema = new GenerateGraphQLSchema(this.generateCodeDocumentParser,
					this.graphqlUtils, generateGraphQLSchemaConf, this.resourceSchemaStringProvider);
			generateGraphQLSchema.generateGraphQLSchema();
		}

		return ret;

	}

	/**
	 * This method makes sure that the schema files are available to spring-graphql at runtime. So either the schema
	 * files are in the src/main/resources/graphql folder of the current project, or they will be copied into the
	 * {generatesResource}/graphql folder. <br/>
	 * This is useful only for the server mode.
	 * 
	 * @throws IOException
	 */
	private void copySchemaFilesToGraphqlFolder() throws IOException {
		String standardSpringGraphqlSchemaPath = new File(this.configuration.getProjectDir(),
				"src/main/resources/graphql").getCanonicalPath(); //$NON-NLS-1$
		if (!this.configuration.getSchemaFileFolder().getCanonicalPath().equals(standardSpringGraphqlSchemaPath)) {
			// The schema file(s) is(are) not where spring-graphql expects it (that is in the graphql of the
			// classpath).
			// So we copy it/them in the correct location
			for (Resource r : this.resourceSchemaStringProvider.schemas(false)) {
				File folder = new File(this.configuration.getTargetResourceFolder(),
						this.configuration.getTargetSchemaSubFolder());
				File f = new File(folder, r.getFilename());

				logger.debug("Copying {} from  {} to {}", r.getFilename(), r.getFile().getAbsolutePath(), //$NON-NLS-1$
						f.getAbsolutePath());

				f.getParentFile().mkdirs();
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, false))) {
					writer.append(this.resourceSchemaStringProvider.readSchema(r));
				}
			}
		}
	}

	/**
	 * Generates one file from the given parameter, based on a Velocity context and template.
	 * 
	 * @param classname
	 *            The classname for the file to be generated. It will be either created or rewrited
	 * @param utilityClass
	 *            true if this class is utility class.
	 * @param msg
	 *            A log message. It will be logged in debug mode, or send as the error message if an exception is
	 *            raised.
	 * @param context
	 *            The Velocity context
	 * @param templateCode
	 *            The Velocity template to use
	 * @return The number of classes created, that is: 1
	 */
	int generateOneJavaFile(String classname, boolean utilityClass, String msg, VelocityContext context,
			CodeTemplate templateCode) {

		context.put("targetFileName", classname); //$NON-NLS-1$

		File targetFile = getJavaFile(classname, utilityClass);
		logger.debug("Generating {} into {}", msg, targetFile); //$NON-NLS-1$
		targetFile.getParentFile().mkdirs();

		return generateOneFile(targetFile, msg, context, templateCode);
	}

	/**
	 * Generates one resource file from the given parameter, based on a Velocity context and template.
	 * 
	 * @param targetFile
	 *            The file to be generated. It will be either created or rewrited
	 * @param msg
	 *            A log message. It will be logged in debug mode, or send as the error message if an exception is
	 *            raised.
	 * @param context
	 *            The Velocity context
	 * @param templateCode
	 *            The Velocity template to use
	 * @return The number of classes created, that is: 1
	 */
	int generateOneFile(File targetFile, String msg, VelocityContext context, CodeTemplate templateCode) {
		Template template = null;
		String theTemplate = null;
		String resolvedTemplate = resolveTemplate(templateCode);

		try {
			template = this.velocityEngine.getTemplate(resolvedTemplate, "UTF-8"); //$NON-NLS-1$
		} catch (ResourceNotFoundException e) {
			// When in Gradle, Velocity doesn't seem to be able to load templates that are packaged in another jar. So
			// we load these templates with a spring resource
			try {
				Resource resource = this.ctx.getResource("classpath:" + resolvedTemplate); //$NON-NLS-1$
				try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF_8")) { //$NON-NLS-1$
					theTemplate = FileCopyUtils.copyToString(reader);
				}
				StringResourceLoader.getRepository().putStringResource(theTemplate, resolvedTemplate);
			} catch (Exception e2) {
				logger.warn("Could not load the resource in a the Spring resource. Got this exception: " //$NON-NLS-1$
						+ e2.getClass().getSimpleName() + " (" + e2.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				// If we can't load the resource here, we send the original exception.
				throw new ResourceNotFoundException(e.getMessage(), e);
			}
		}

		try {

			logger.debug("Generating {} into {}", msg, targetFile); //$NON-NLS-1$
			targetFile.getParentFile().mkdirs();

			try (Writer writer = (this.configuration.getSourceEncoding() != null)
					? new OutputStreamWriter(new FileOutputStream(targetFile),
							Charset.forName(this.configuration.getSourceEncoding()))
					: new OutputStreamWriter(new FileOutputStream(targetFile))) {
				template.merge(context, writer);
				writer.flush();
			}

			// Let's return the number of created files. That is: 1.
			// Not very useful. But it helps making simpler the code of the caller for this method
			return 1;
		} catch (ResourceNotFoundException | ParseErrorException | TemplateInitException | MethodInvocationException
				| IOException e) {
			throw new RuntimeException("Error when " + msg + "; " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
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
		String packageName;

		if (simpleClassname.startsWith(SPRING_AUTO_CONFIGURATION_CLASS)) {
			packageName = this.configuration.getSpringAutoConfigurationPackage();
		} else {
			packageName = (utilityClass && this.configuration.isSeparateUtilityClasses())
					? this.generateCodeDocumentParser.getUtilPackageName()
					: this.configuration.getPackageName();
		}

		String relativePath = packageName.replace('.', '/') + '/' + simpleClassname + ".java"; //$NON-NLS-1$
		File file = new File(this.configuration.getTargetSourceFolder(), relativePath);
		file.getParentFile().mkdirs();
		return file;
	}

	/**
	 * This method returns the {@link File} where this resource must be generated. It adds the preceding path, and
	 * creates the missing folders, if any
	 * 
	 * @param filename
	 *            The relative filename, starting from the root of the class file (for instance:
	 *            META-INF/spring.factories)
	 * @return
	 */
	File getResourceFile(String filename) {
		File file = new File(this.configuration.getTargetResourceFolder(), filename);
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
	@SuppressWarnings("static-method")
	Object exec(String methodName, Object object) {
		try {
			Method getType = object.getClass().getMethod(methodName);
			return getType.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' on '" //$NON-NLS-1$ //$NON-NLS-2$
					+ object.getClass().getName() + "': " + e.getMessage(), e); //$NON-NLS-1$
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
	@SuppressWarnings("static-method")
	<T> Object execWithOneParam(String methodName, Object object, T param, Class<T> clazz) {
		try {
			Method getType = object.getClass().getMethod(methodName, clazz);
			return getType.invoke(object, param);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' (with a String param) on '" //$NON-NLS-1$ //$NON-NLS-2$
					+ object.getClass().getName() + "': " + e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns a {@link VelocityContext} with all default values filled.
	 * 
	 * @return
	 */
	VelocityContext getVelocityContext() {
		VelocityContext context = new VelocityContext();
		context.put("carriageReturn", "\r"); //$NON-NLS-1$ //$NON-NLS-2$
		context.put("configuration", this.configuration); //$NON-NLS-1$
		context.put("dollar", "$"); //$NON-NLS-1$ //$NON-NLS-2$
		context.put("lineFeed", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		context.put("exceptionThrower", new ExceptionThrower()); //$NON-NLS-1$
		context.put("graphqlUtils", this.graphqlUtils); //$NON-NLS-1$
		context.put("javaKeywordPrefix", GraphqlUtils.JAVA_KEYWORD_PREFIX); //$NON-NLS-1$
		context.put("sharp", "#"); //$NON-NLS-1$ //$NON-NLS-2$
		context.put("velocityUtils", VelocityUtils.velocityUtils); //$NON-NLS-1$

		// Velocity can't access to enum values. So we add it into the context
		context.put("isPluginModeClient", Boolean.valueOf(this.configuration.getMode() == PluginMode.client)); //$NON-NLS-1$

		context.put("packageUtilName", this.generateCodeDocumentParser.getUtilPackageName()); //$NON-NLS-1$
		context.put("customScalars", this.generateCodeDocumentParser.getCustomScalars()); //$NON-NLS-1$
		context.put("directives", this.generateCodeDocumentParser.getDirectives()); //$NON-NLS-1$
		return context;
	}

	/**
	 * @return
	 */
	private VelocityContext getVelocityServerContext() {
		if (this.serverContext == null) {
			this.serverContext = getVelocityContext();
			this.serverContext.put("dataFetchersDelegates", this.generateCodeDocumentParser.getDataFetchersDelegates()); //$NON-NLS-1$
			this.serverContext.put("batchLoaders", this.generateCodeDocumentParser.getBatchLoaders()); //$NON-NLS-1$
			this.serverContext.put("interfaces", this.generateCodeDocumentParser.getInterfaceTypes()); //$NON-NLS-1$
			this.serverContext.put("unions", this.generateCodeDocumentParser.getUnionTypes()); //$NON-NLS-1$
		}
		return this.serverContext;
	}

	/**
	 * Resolves the template for the given code
	 * 
	 * @param templateCode
	 * @return
	 */
	protected String resolveTemplate(CodeTemplate templateCode) {
		if (this.configuration.getTemplates().containsKey(templateCode.name())) {
			return this.configuration.getTemplates().get(templateCode.name());
		} else {
			return templateCode.getDefaultPath();
		}
	}
}
