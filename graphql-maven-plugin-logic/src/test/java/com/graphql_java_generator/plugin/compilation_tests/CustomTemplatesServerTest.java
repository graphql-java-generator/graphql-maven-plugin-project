package com.graphql_java_generator.plugin.compilation_tests;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin_notscannedbyspring.CustomTemplates_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { CustomTemplates_Server_SpringConfiguration.class })
class CustomTemplatesServerTest extends AbstractCustomTemplateIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is to have its own Spring Configuration
	@BeforeEach
	public void setUp() throws IOException {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases.graphqls");
	}

	/**
	 * This test will be executed for each concrete subclass of this class
	 * 
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	@Override
	@Test
	@DirtiesContext // We need to forget the previous parsing (or everything may be doubled)
	void testGenerateCode() throws IOException {
		super.testGenerateCode();

		// Validate that every file generated has been generated with the templates in
		// src/test/resources/templates_personalization
		File generatedSourcesDir = new File(this.pluginConfiguration.getTargetSourceFolder(),
				pluginConfiguration.getPackageName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
		assertCustomTemplateGeneration(generatedSourcesDir);

	}

}
