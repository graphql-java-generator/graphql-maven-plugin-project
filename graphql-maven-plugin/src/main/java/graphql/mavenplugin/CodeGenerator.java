/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

/**
 * This class generates the code, from the classes coming from the graphql.mavenplugin.language package. This classes
 * have been created by {link {@link DocumentParser}
 * 
 * @author EtienneSF
 */
@Component
public class CodeGenerator {

	private static final String PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION = "templates/query_mutation_subscription_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_OBJECT = "templates/object_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_INTERFACE = "templates/interface_type.vm.java";
	private static final String PATH_VELOCITY_TEMPLATE_ENUM = "templates/enum_type.vm.java";

	@Resource
	DocumentParser documentParser;

	/** The maven logging system */
	@Resource
	Log log;

	/** @See GraphqlMavenPlugin#basePackage */
	@Resource
	String basePackage;

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
	 */
	public int generateCode() throws MojoExecutionException {
		int i = 0;
		i += generateTargetFile(documentParser.getQueryTypes(), "query",
				PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
		i += generateTargetFile(documentParser.getMutationTypes(), "mutation",
				PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
		i += generateTargetFile(documentParser.getSubscriptionTypes(), "subscription",
				PATH_VELOCITY_TEMPLATE_QUERY_MUTATION_SUBSCRIPTION);
		i += generateTargetFile(documentParser.getObjectTypes(), "object", PATH_VELOCITY_TEMPLATE_OBJECT);
		i += generateTargetFile(documentParser.getInterfaceTypes(), "interface", PATH_VELOCITY_TEMPLATE_INTERFACE);
		i += generateTargetFile(documentParser.getEnumTypes(), "enum", PATH_VELOCITY_TEMPLATE_ENUM);
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
	int generateTargetFile(List<?> objects, String type, String templateFilename) throws RuntimeException {
		int i = 0;
		for (Object object : objects) {
			i += 1;
			File targetFile = getJavaFile((String) exec("getName", object));
			String msg = "Generating " + type + " '" + object + "' into " + targetFile.getAbsolutePath();
			try {
				VelocityContext context = new VelocityContext();
				context.put("package", basePackage);
				context.put("object", object);
				context.put("type", type);
				log.debug(msg);
				Template template = velocityEngine.getTemplate(templateFilename);

				targetFile.getParentFile().mkdirs();
				FileWriter writer = new FileWriter(targetFile);
				template.merge(context, writer);
				writer.flush();
				writer.close();
			} catch (ResourceNotFoundException | ParseErrorException | TemplateInitException | MethodInvocationException
					| IOException e) {
				throw new RuntimeException("Error when " + msg, e);
			}
		} // for
		return i;
	}

	/**
	 * This method returns the {@link File} where the class is to be generated
	 * 
	 * @param simpleClassname
	 *            The classname, without the package
	 * @return
	 */
	File getJavaFile(String simpleClassname) {
		String relativePath = basePackage.replace('.', '/') + '/' + simpleClassname + ".java";
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
			Method getType = object.getClass().getDeclaredMethod(methodName);
			return getType.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' on '"
					+ object.getClass().getName() + "': " + e.getMessage(), e);
		}
	}
}
