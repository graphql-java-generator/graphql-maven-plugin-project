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
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.CustomScalarType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

import graphql.schema.GraphQLScalarType;

/**
 * This class generates the code, from the classes coming from the com.graphql_java_generator.plugin.language package.
 * This classes have been created by {link {@link DocumentParser}
 * 
 * @author EtienneSF
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

		int i = 0;
		i += generateTargetFiles(documentParser.getObjectTypes(), "object", resolveTemplate(CodeTemplate.OBJECT));
		i += generateTargetFiles(documentParser.getInterfaceTypes(), "interface",
				resolveTemplate(CodeTemplate.INTERFACE));
		i += generateTargetFiles(documentParser.getUnionTypes(), "union", resolveTemplate(CodeTemplate.UNION));
		i += generateTargetFiles(documentParser.getEnumTypes(), "enum", resolveTemplate(CodeTemplate.ENUM));

		switch (pluginConfiguration.getMode()) {
		case server:
			i += generateServerFiles();
			break;
		case client:
			// Generation of the query/mutation/subscription classes
			i += generateTargetFiles(documentParser.getQueryTypes(), "query",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_SUBSCRIPTION));
			i += generateTargetFiles(documentParser.getMutationTypes(), "mutation",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_SUBSCRIPTION));
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "subscription",
					resolveTemplate(CodeTemplate.QUERY_MUTATION_SUBSCRIPTION));

			// Generation of the query/mutation/subscription response classes
			i += generateTargetFiles(documentParser.getQueryTypes(), "response", resolveTemplate(CodeTemplate.OBJECT));
			i += generateTargetFiles(documentParser.getMutationTypes(), "response",
					resolveTemplate(CodeTemplate.OBJECT));
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "response",
					resolveTemplate(CodeTemplate.OBJECT));

			// Generation of the query/mutation/subscription root responses classes
			i += generateTargetFiles(documentParser.getQueryTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE));
			i += generateTargetFiles(documentParser.getMutationTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE));
			i += generateTargetFiles(documentParser.getSubscriptionTypes(), "root response",
					resolveTemplate(CodeTemplate.ROOT_RESPONSE));
			
			// Generation of the GraphQLRequest class
			i += generateGraphQLRequest();			

			// Files for Custom Scalars
			VelocityContext context = new VelocityContext();
			context.put("pluginConfiguration", pluginConfiguration);
			context.put("customScalars", documentParser.customScalars);
			i += generateOneFile(getJavaFile("CustomScalarRegistryInitializer"),
					"Generating CustomScalarRegistryInitializer", context,
					resolveTemplate(CodeTemplate.CUSTOM_SCALAR_REGISTRY_INITIALIZER));
			// Files for Directives
			context = new VelocityContext();
			context.put("pluginConfiguration", pluginConfiguration);
			context.put("directives", documentParser.directives);
			i += generateOneFile(getJavaFile("DirectiveRegistryInitializer"), "Generating DirectiveRegistryInitializer",
					context, resolveTemplate(CodeTemplate.DIRECTIVE_REGISTRY_INITIALIZER));
			//
			i += generateTargetFiles(documentParser.customScalars, FILE_TYPE_JACKSON_DESERIALIZER,
					resolveTemplate(CodeTemplate.JACKSON_DESERIALIZER));
			i += generateQueryTargetType();
			break;
		}

		if (pluginConfiguration.isCopyGraphQLJavaSources()) {
			copyGraphQLJavaSources();
		}

		return i;

	}

	void copyGraphQLJavaSources() throws IOException {
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

				if (!metaInf && !serverAndIsClientFile) {
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
	 * @throws IOException
	 */
	int generateTargetFiles(List<? extends Type> objects, String type, String templateFilename)
			throws RuntimeException {
		int i = 0;
		for (Type object : objects) {
			String targetFileName = (String) execWithOneStringParam("getTargetFileName", object, type);
			File targetFile = getJavaFile(targetFileName);
			String msg = "Generating " + type + " '" + object.getName() + "' into " + targetFile.getAbsolutePath();

			VelocityContext context = new VelocityContext();
			context.put("imports", getImportList());
			context.put("object", object);
			context.put("pluginConfiguration", pluginConfiguration);
			context.put("targetFileName", targetFileName);
			context.put("type", type);

			i += generateOneFile(targetFile, msg, context, templateFilename);
		} // for
		return i;
	}

	/**
	 * Generates one wrapper for each query, that will receive the response json.
	 * 
	 * @return
	 */
	int generateQueryTargetType() {
		int i = 0;
		List<ObjectType> types = new ArrayList<>(documentParser.getQueryTypes());
		types.addAll(documentParser.getMutationTypes());
		types.addAll(documentParser.getSubscriptionTypes());

		for (ObjectType queryType : types) {
			for (Field query : queryType.getFields()) {
				String objectName = queryType.getClassSimpleName() + query.getPascalCaseName();
				File targetFile = getJavaFile(objectName);
				String msg = "Generating target for query " + query.getName() + " '" + objectName + "' into "
						+ targetFile.getAbsolutePath();
				VelocityContext context = new VelocityContext();
				context.put("pluginConfiguration", pluginConfiguration);
				context.put("objectName", objectName);
				context.put("query", query);

				i += generateOneFile(targetFile, msg, context, resolveTemplate(CodeTemplate.QUERY_TARGET_TYPE));
			}
		}
		return i;
	}

	/**
	 * Generates the {@link GraphQLRequest} class . This method expects at most one query, one mutation and one
	 * subscription, which is compliant with the GraphQL specification
	 */
	int generateGraphQLRequest() {
		VelocityContext context = new VelocityContext();
		context.put("pluginConfiguration", pluginConfiguration);

		context.put("query", (documentParser.queryTypes.size() > 0) ? documentParser.queryTypes.get(0) : null);
		context.put("mutation", (documentParser.mutationTypes.size() > 0) ? documentParser.mutationTypes.get(0) : null);
		context.put("subscription",
				(documentParser.subscriptionTypes.size() > 0) ? documentParser.subscriptionTypes.get(0) : null);

		return generateOneFile(getJavaFile("GraphQLRequest"), "generating GraphQLRequest", context,
				resolveTemplate(CodeTemplate.GRAPHQL_REQUEST));
	}

	/**
	 * Generates the server classes
	 * 
	 * @return The number of classes created, that is: 1
	 * @throws IOException
	 */
	int generateServerFiles() throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("pluginConfiguration", pluginConfiguration);
		context.put("dataFetchersDelegates", documentParser.getDataFetchersDelegates());
		context.put("interfaces", documentParser.getInterfaceTypes());
		context.put("unions", documentParser.getUnionTypes());
		context.put("imports", getImportList());
		context.put("customScalars", documentParser.customScalars);

		// List of found schemas
		List<String> schemaFiles = new ArrayList<>();
		for (org.springframework.core.io.Resource res : resourceSchemaStringProvider.schemas()) {
			schemaFiles.add(res.getFilename());
		}
		context.put("schemaFiles", schemaFiles);

		int ret = 0;
		ret += generateOneFile(getJavaFile("GraphQLServerMain"), "generating GraphQLServerMain", context,
				resolveTemplate(CodeTemplate.SERVER));
		ret += generateOneFile(getJavaFile("GraphQLProvider"), "generating GraphQLProvider", context,
				resolveTemplate(CodeTemplate.PROVIDER));
		ret += generateOneFile(getJavaFile("GraphQLDataFetchers"), "generating GraphQLDataFetchers", context,
				resolveTemplate(CodeTemplate.DATAFETCHER));
		ret += generateOneFile(getJavaFile("GraphQLUtil"), "generating GraphQLUtil", context,
				resolveTemplate(CodeTemplate.GRAPHQLUTIL));

		for (DataFetchersDelegate dataFetcherDelegate : documentParser.dataFetchersDelegates) {
			context.put("dataFetcherDelegate", dataFetcherDelegate);
			ret += generateOneFile(getJavaFile(dataFetcherDelegate.getPascalCaseName()),
					"generating " + dataFetcherDelegate.getPascalCaseName(), context,
					resolveTemplate(CodeTemplate.DATAFETCHERDELEGATE));
		}

		ret += generateOneFile(getJavaFile("BatchLoaderDelegate"), "generating BatchLoaderDelegate", context,
				resolveTemplate(CodeTemplate.BATCHLOADERDELEGATE));
		for (BatchLoader batchLoader : documentParser.batchLoaders) {
			String name = "BatchLoaderDelegate" + batchLoader.getType().getClassSimpleName() + "Impl";
			context.put("batchLoader", batchLoader);
			ret += generateOneFile(getJavaFile(name), "generating " + name, context,
					resolveTemplate(CodeTemplate.BATCHLOADERDELEGATEIMPL));
		}

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
	 * @return
	 */
	File getJavaFile(String simpleClassname) {
		String relativePath = pluginConfiguration.getPackageName().replace('.', '/') + '/' + simpleClassname + ".java";
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
	 * Retrieves all the class that must be imported. This list is based on the list of Custom Scalars, that may need
	 * specific import, for specific {@link GraphQLScalarType}.
	 */
	List<String> getImportList() {
		List<String> ret = new ArrayList<>();
		if (documentParser.customScalars != null) {
			for (CustomScalarType customScalar : documentParser.customScalars) {
				if (!customScalar.getPackageName().contentEquals("java.lang")) {
					ret.add(customScalar.getClassFullName());
				}
			}
		}
		return ret;
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
