/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

import graphql.mavenplugin.language.EnumType;
import graphql.mavenplugin.language.ObjectType;

/**
 * This class generates the code, from the classes coming from the graphql.mavenplugin.language package. This classes
 * have been created by {link {@link DocumentParser}
 * 
 * @author EtienneSF
 */
public class CodeGenerator {

	private static final String PATH_VELOCITY_TEMPLATE_QUERY = "templates/query_type.vm.java";
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
	 * @throws MojoExecutionException
	 */
	public void generateCode() throws MojoExecutionException {
		generateQueryTypes();
		generateObjectTypes();
		generateInterfaceTypes();
		generateEnumTypes();
	}

	void generateQueryTypes() throws MojoExecutionException {
		VelocityContext context = new VelocityContext();
		for (ObjectType query : documentParser.getQueryTypes()) {
			context.put("query", query);
			File file = getJavaFile(query.getName());
			log.debug("Generating query '" + query + "' into " + file.getAbsolutePath());
			generateTargetFile(PATH_VELOCITY_TEMPLATE_QUERY, context, file);
		} // for
	}

	void generateObjectTypes() throws MojoExecutionException {
		VelocityContext context = new VelocityContext();
		for (ObjectType object : documentParser.getObjectTypes()) {
			context.put("object", object);
			File file = getJavaFile(object.getName());
			if (log.isDebugEnabled()) {
				log.debug("Generating object '" + object.getName() + "' into " + file.getAbsolutePath());
			}
			generateTargetFile(PATH_VELOCITY_TEMPLATE_OBJECT, context, file);
		} // for
	}

	void generateInterfaceTypes() throws MojoExecutionException {
		VelocityContext context = new VelocityContext();
		// Let's go through all objects and interfaces
		for (ObjectType interfaceType : documentParser.getInterfaceTypes()) {
			context.put("interface", interfaceType);
			File file = getJavaFile(interfaceType.getName());
			if (log.isDebugEnabled()) {
				log.debug("Generating interface '" + interfaceType.getName() + "' into " + file.getAbsolutePath());
			}
			generateTargetFile(PATH_VELOCITY_TEMPLATE_INTERFACE, context, file);
		} // for
	}

	void generateEnumTypes() throws MojoExecutionException {
		VelocityContext context = new VelocityContext();
		for (EnumType enumType : documentParser.getEnumTypes()) {
			context.put("enum", enumType);
			File file = getJavaFile(enumType.getName());
			log.debug("Generating enum '" + enumType + "' into " + file.getAbsolutePath());
			generateTargetFile(PATH_VELOCITY_TEMPLATE_ENUM, context, file);
		} // for
	}

	/**
	 * Utility method to centralize the common actions around the file generation.
	 * 
	 * @param templateFilename
	 *            The absolute path for the template (or relative to the current path)
	 * @param context
	 *            The initialized context, with all parameters to send to the template
	 * @param targetFile
	 *            The target file
	 * @throws IOException
	 */
	void generateTargetFile(String templateFilename, VelocityContext context, File targetFile) throws RuntimeException {
		String msg = "Writing " + targetFile.getAbsolutePath();
		try {
			log.debug(msg);

			// Let's add some specific stuff, as escaping in Velocity doesn't work
			context.put("package", basePackage);
			// context.put("dollar", "$");
			context.put("openCurlyBrace", "{");
			context.put("closeCurlyBrace", "{");

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

}
