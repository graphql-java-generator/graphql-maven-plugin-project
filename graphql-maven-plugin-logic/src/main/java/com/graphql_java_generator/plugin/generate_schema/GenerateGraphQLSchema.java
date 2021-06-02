/**
 * 
 */
package com.graphql_java_generator.plugin.generate_schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.generate_code.GenerateCodeGenerator;
import com.graphql_java_generator.util.GraphqlUtils;

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
public class GenerateGraphQLSchema {

	private static final Logger logger = LoggerFactory.getLogger(GenerateGraphQLSchema.class);

	final DocumentParser documentParser;

	final GraphqlUtils graphqlUtils;

	/** The component that reads the GraphQL schema from the file system */
	final ResourceSchemaStringProvider resourceSchemaStringProvider;

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	final GenerateGraphQLSchemaConfiguration configuration;

	/** The Velocity engine used to generate the target file */
	VelocityEngine velocityEngine = null;

	/**
	 * The constructor that Spring will use to load this Spring bean
	 * 
	 * @param documentParser
	 *            The document parser, that loads the GraphQL schema into memory, and prepares the data for the schema
	 *            generation.
	 * @param graphqlUtils
	 *            A runtime utility class
	 * @param configuration
	 *            The configuration for the <I>generateGraphQLSchema</I> task/goal
	 */
	@Autowired
	public GenerateGraphQLSchema(GenerateGraphQLSchemaDocumentParser documentParser, GraphqlUtils graphqlUtils,
			GenerateGraphQLSchemaConfiguration configuration,
			ResourceSchemaStringProvider resourceSchemaStringProvider) {
		this.documentParser = documentParser;
		this.graphqlUtils = graphqlUtils;
		this.configuration = configuration;
		this.resourceSchemaStringProvider = resourceSchemaStringProvider;
	}

	/**
	 * A constructor that can be called by other tasks/goals. For instance, the {@link GenerateCodeGenerator} class
	 * creates an instance of this class, when in server mode and addRelayConnections is true, to generate the GraphQL
	 * schema, as it is necessary for the graphql-java at runtime.
	 * 
	 * @param documentParser
	 * @param graphqlUtils
	 * @param configuration
	 */
	public GenerateGraphQLSchema(DocumentParser documentParser, GraphqlUtils graphqlUtils,
			GenerateGraphQLSchemaConfiguration configuration,
			ResourceSchemaStringProvider resourceSchemaStringProvider) {
		this.documentParser = documentParser;
		this.graphqlUtils = graphqlUtils;
		this.configuration = configuration;
		this.resourceSchemaStringProvider = resourceSchemaStringProvider;
	}

	/** This method is the entry point, for the generation of the schema that merges the GraphQL source schema files */
	public void generateGraphQLSchema() {
		String msg = null;
		try {

			File targetFile = new File(configuration.getTargetFolder(), configuration.getTargetSchemaFileName());
			msg = "Generating relay schema in this file: " + targetFile.getAbsolutePath();
			logger.debug(msg);

			VelocityContext context = new VelocityContext();
			context.put("newline", "\n");
			context.put("space", " ");
			context.put("documentParser", documentParser);
			context.put("graphqlUtils", graphqlUtils);
			//
			context.put("scalars", documentParser.getScalarTypes());
			context.put("customScalars", documentParser.getCustomScalars());
			context.put("directives", documentParser.getDirectives());
			context.put("enumTypes", documentParser.getEnumTypes());
			context.put("interfaceTypes", documentParser.getInterfaceTypes());
			context.put("mutationType", documentParser.getMutationType());
			context.put("objectTypes", documentParser.getObjectTypes());
			context.put("queryType", documentParser.getQueryType());
			context.put("subscriptionType", documentParser.getSubscriptionType());
			context.put("unionTypes", documentParser.getUnionTypes());
			Template template = getVelocityEngine().getTemplate(resolveTemplate(CodeTemplate.RELAY_SCHEMA), "UTF-8");

			targetFile.getParentFile().mkdirs();
			Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile),
					Charset.forName(configuration.getResourceEncoding()));
			template.merge(context, writer);
			writer.flush();
			writer.close();

			logger.info("The GraphQL schema has been generated in '" + targetFile.getAbsolutePath() + "'");

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

	private VelocityEngine getVelocityEngine() {
		if (velocityEngine == null) {
			// Initialization for Velocity
			velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			velocityEngine.init();
		}
		return velocityEngine;
	}
}
