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

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.MergeSchemaConfiguration;

/**
 * This class merges one or more given GraphQL schema files into a new GraphQL schema, that is written in the given
 * target schema file. If {@link CommonConfiguration#isAddRelayConnections()} is true, then the generated schema is
 * updated to be conform to the <A HREF="https://relay.dev/graphql/connections.htm">relay connection
 * specification</A>.<BR/>
 * The job is done by using this class as a Spring bean, and calling its {@link #generateGraphQLSchema()} method.
 * 
 * @author etienne-sf
 *
 */
@Component
public class Merge {

	@Autowired
	MergeDocumentParser documentParser;

	@Autowired
	GraphqlUtils graphqlUtils;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	MergeSchemaConfiguration configuration;

	/** The Velocity engine used to generate the target file */
	VelocityEngine velocityEngine = null;

	public Merge() {
		// Initialization for Velocity
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	/** This method is the entry point, for the generation of the schema that merges the GraphQL source schema files */
	public void generateGraphQLSchema() {

		String msg = null;
		try {
			File targetFile = new File(configuration.getTargetFolder(), configuration.getTargetSchemaFileName());
			msg = "Generating relay schema in this file: " + targetFile.getAbsolutePath();
			configuration.getLog().debug(msg);

			VelocityContext context = new VelocityContext();
			context.put("newline", "\n");
			context.put("space", " ");
			context.put("documentParser", documentParser);
			context.put("graphqlUtils", graphqlUtils);
			//
			context.put("customScalars", documentParser.customScalars);
			context.put("directives", documentParser.directives);
			context.put("enumTypes", documentParser.enumTypes);
			context.put("interfaceTypes", documentParser.interfaceTypes);
			context.put("mutationType", documentParser.mutationType);
			context.put("objectTypes", documentParser.objectTypes);
			context.put("queryType", documentParser.queryType);
			context.put("subscriptionType", documentParser.subscriptionType);
			context.put("unionTypes", documentParser.unionTypes);
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
