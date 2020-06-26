/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

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
import org.springframework.stereotype.Component;

/**
 * This class generates the relay schema, based on the given GraphQL schema(s). That is: it generates a GraphQL schema,
 * that is compliant with relay pagination, from the given GraphQL schema.<BR/>
 * The job is done by using this class as a Spring bean, and calling its {@link #generateRelaySchema()} method.
 * 
 * @author etienne-sf
 *
 */
@Component
public class GenerateRelaySchema {

	@Autowired
	GenerateRelaySchemaDocumentParser documentParser;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GenerateRelaySchemaConfiguration configuration;

	/** The Velocity engine used to generate the target file */
	VelocityEngine velocityEngine = null;

	public GenerateRelaySchema() {
		// Initialization for Velocity
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	/** This method is the entry point, for the generation of the relay compatible schema */
	public void generateRelaySchema() {

		// Let's first read the source GraphQL schemas
		documentParser.parseDocuments();

		String msg = null;
		try {
			File targetFile = new File(configuration.getTargetFolder(), configuration.getSchemaFileName());
			msg = "Generating relay schema in this file: " + targetFile.getAbsolutePath();
			configuration.getLog().debug(msg);

			VelocityContext context = new VelocityContext();
			context.put("customScalars", documentParser.customScalars);
			context.put("queryType", documentParser.queryType);
			context.put("mutationType", documentParser.mutationType);
			context.put("subscriptionType", documentParser.subscriptionType);
			Template template = velocityEngine.getTemplate(resolveTemplate(CodeTemplate.RELAY_SCHEMA), "UTF-8");

			targetFile.getParentFile().mkdirs();
			Writer writer = new FileWriterWithEncoding(targetFile,
					Charset.forName(configuration.getResourceEncoding()));
			template.merge(context, writer);
			writer.flush();
			writer.close();
		} catch (ResourceNotFoundException | ParseErrorException | TemplateInitException | MethodInvocationException
				| IOException e) {
			throw new RuntimeException("Error when " + msg, e);
		}
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
