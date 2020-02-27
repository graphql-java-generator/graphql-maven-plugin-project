package com.graphql_java_generator.plugin;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This component is the unique entry point that will execute the code generation. It is called by the maven plugin and
 * the gradle plugin.<BR/>
 * It is responsible to execute all necessary actions, including:
 * <UL>
 * <LI>Parsing the given GraphQL schema(s)</LI>
 * <LI>Generating the code for them</LI>
 * <LI>Repeating that for the introspection GraphQL capability</LI>
 * </UL>
 * 
 * @author etienne-sf
 *
 */

@Component
public class PluginEntryPoint {

	private static final String INTROSPECTION_SCHEMA = "classpath:/introspection.graphqls";
	private static final String INTROSPECTION_PACKAGE = "com.graphql_java_generator.client.introspection";

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	PluginConfiguration pluginConfiguration;

	@Resource
	DocumentParser documentParser;

	@Resource
	CodeGenerator codeGenerator;

	@Autowired
	BeanFactory beanFactory;

	public void execute() throws IOException {

		// Generation of the code for the configured GraphQL schema(s)
		documentParser.parseDocuments();
		int nbGeneratedClasses = codeGenerator.generateCode();
		pluginConfiguration.getLog()
				.info(nbGeneratedClasses + " java classes have been generated from the schema(s) '"
						+ pluginConfiguration.getSchemaFilePattern() + "' in the package '"
						+ pluginConfiguration.getPackageName() + "'");

		// Generation of the code for the introspection schema
		if (pluginConfiguration.getMode().equals(PluginMode.client)) {

			pluginConfiguration.setSchemaFilePattern(INTROSPECTION_SCHEMA);
			pluginConfiguration.setPackageName(INTROSPECTION_PACKAGE);

			// We need to clean the previous parsing before a new execution
			documentParser.initialize();

			// Then, we can start the job
			documentParser.parseDocuments();
			nbGeneratedClasses = codeGenerator.generateCode();
			pluginConfiguration.getLog()
					.info(nbGeneratedClasses + " java classes have been generated from the schema(s) '"
							+ pluginConfiguration.getSchemaFilePattern() + "' in the package '"
							+ pluginConfiguration.getPackageName() + "'");
		}
	}

}
