package com.graphql_java_generator.plugin.compilation_tests;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import graphql.mavenplugin_notscannedbyspring.CustomTemplates_Client_SpringConfiguration;

@Tag("customTemplates")
@Execution(ExecutionMode.CONCURRENT)
class CustomTemplatesClientTest extends AbstractCustomTemplateIntegrationTest {

	public CustomTemplatesClientTest() {
		super(CustomTemplates_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() throws IOException {
		this.graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases.graphqls");
	}

	/**
	 * This test will be executed for each concrete subclass of this class
	 * 
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	@Override
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateCode() throws Exception {
		super.testGenerateCode();

		// Validate that every file generated has been generated with the templates in
		// src/test/resources/templates_personalization
		File generatedSourcesDir = new File(this.configuration.getTargetSourceFolder(),
				this.configuration.getPackageName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
		assertCustomTemplateGeneration(generatedSourcesDir);

	}

}
