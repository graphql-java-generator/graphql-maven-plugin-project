/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Component;

import graphql.mavenplugin.language.DataFetcherDelegate;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.Type;
import graphql.mavenplugin.language.impl.ObjectType;

/**
 * This class generates the code, from the classes coming from the graphql.mavenplugin.language package. This classes
 * have been created by {link {@link DocumentParser}
 * 
 * @author EtienneSF
 */
@Component
public class CodeGenerator {

	// Templates for both client and server generation
	private static final String PATH_VELOCITY_TEMPLATE_OBJECT = "templates/object_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_INTERFACE = "templates/interface_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_ENUM = "templates/enum_type.vm.java";
	// Templates for client generation only
	private static final String PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION = "templates/client_query_mutation_subscription_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_QUERY_TARGET_TYPE = "templates/client_query_target_type.vm.java";
	// Templates for server generation only
	private static final String PATH_VELOCITY_TEMPLATE_DATAFETCHER = "templates/server_GraphQLDataFetchers.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_PROVIDER = "templates/server_GraphQLProvider.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_SERVER = "templates/server_GraphQLServer.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_DATAFETCHERDELEGATE = "templates/server_GraphQLDataFetchersDelegate.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_GRAPHQLUTIL = "templates/server_GraphQLUtil.vm.java";

	@Resource
	MavenResourceSchemaStringProvider mavenResourceSchemaStringProvider;

	@Resource
	DocumentParser documentParser;

	/** The maven logging system */
	@Resource
	Log log;

	/** @See {@link GraphqlMavenPlugin#mode} */
	@Resource
	PluginMode mode;

	/** @See GraphqlMavenPlugin#packageName */
	@Resource
	String packageName;

	/** @See {@link GraphqlMavenPlugin#targetSourceFolder} */
	@Resource
	File targetSourceFolder;

	/** @See {@link GraphqlMavenPlugin#encoding} */
	@Resource
	String encoding;

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
	public int generateCode() throws MojoExecutionException, IOException {

		int i = 0;
		i += generateTargetFile(documentParser.getObjectTypes(), "object", PATH_VELOCITY_TEMPLATE_OBJECT);
		i += generateTargetFile(documentParser.getInterfaceTypes(), "interface", PATH_VELOCITY_TEMPLATE_INTERFACE);
		i += generateTargetFile(documentParser.getEnumTypes(), "enum", PATH_VELOCITY_TEMPLATE_ENUM);

		switch (mode) {
		case server:
			i += generateServerFiles();
			break;
		case client:
			i += generateTargetFile(documentParser.getQueryTypes(), "query",
					PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
			i += generateTargetFile(documentParser.getMutationTypes(), "mutation",
					PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
			i += generateTargetFile(documentParser.getSubscriptionTypes(), "subscription",
					PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
			i += generateQueryTargetType();
			break;
		}
		return i;
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
	int generateTargetFile(List<? extends Type> objects, String type, String templateFilename) throws RuntimeException {
		int i = 0;
		for (Type object : objects) {
			File targetFile = getJavaFile((String) exec("getName", object));
			String msg = "Generating " + type + " '" + object.getName() + "' into " + targetFile.getAbsolutePath();
			VelocityContext context = new VelocityContext();
			context.put("package", packageName);
			context.put("object", object);
			context.put("type", type);
			context.put("mode", documentParser.getMode());

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
				context.put("package", packageName);
				context.put("objectName", objectName);
				context.put("query", query);

				i += generateOneFile(targetFile, msg, context, PATH_VELOCITY_TEMPLATE_QUERY_TARGET_TYPE);
			}
		}
		return i;
	}

	/**
	 * Generates the server classes
	 * 
	 * @return The number of classes created, that is: 1
	 * @throws IOException
	 */
	int generateServerFiles() throws IOException {

		VelocityContext context = new VelocityContext();
		context.put("package", packageName);
		context.put("dataFetcherDelegates", documentParser.getDataFetcherDelegates());
		context.put("interfaces", documentParser.getInterfaceTypes());

		// List of found schemas
		List<String> schemaFiles = new ArrayList<>();
		for (org.springframework.core.io.Resource res : mavenResourceSchemaStringProvider.schemas()) {
			schemaFiles.add(res.getFilename());
		}
		context.put("schemaFiles", schemaFiles);

		int ret = 0;
		ret += generateOneFile(getJavaFile("GraphQLServer"), "generating GraphQLServer", context,
				PATH_VELOCITY_TEMPLATE_SERVER);
		ret += generateOneFile(getJavaFile("GraphQLProvider"), "generating GraphQLProvider", context,
				PATH_VELOCITY_TEMPLATE_PROVIDER);
		ret += generateOneFile(getJavaFile("GraphQLDataFetchers"), "generating GraphQLDataFetchers", context,
				PATH_VELOCITY_TEMPLATE_DATAFETCHER);
		ret += generateOneFile(getJavaFile("GraphQLUtil"), "generating GraphQLUtil", context,
				PATH_VELOCITY_TEMPLATE_GRAPHQLUTIL);

		for (DataFetcherDelegate dataFetcherDelegate : documentParser.dataFetcherDelegates) {
			context.put("dataFetcherDelegate", dataFetcherDelegate);
			ret += generateOneFile(getJavaFile(dataFetcherDelegate.getName()),
					"generating " + dataFetcherDelegate.getName(), context, PATH_VELOCITY_TEMPLATE_DATAFETCHERDELEGATE);
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
			log.debug(msg);
			Template template = velocityEngine.getTemplate(templateFilename);

			targetFile.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(targetFile);
			template.merge(context, writer);
			writer.flush();
			writer.close();

			// Let's return the number of created files. That is: 1.
			// Not very useful. But it helps making simpler the code of the caller for this method
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
		String relativePath = packageName.replace('.', '/') + '/' + simpleClassname + ".java";
		File file = new File(targetSourceFolder, relativePath);
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
}
