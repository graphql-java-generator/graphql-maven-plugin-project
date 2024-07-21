package com.graphql_java_generator.plugin.compilation_tests;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import graphql.mavenplugin_notscannedbyspring.CustomTemplates_Server_SpringConfiguration;

@Tag("customTemplates")
class CustomTemplatesServerTest extends AbstractCustomTemplateIntegrationTest {

	public CustomTemplatesServerTest() {
		super(CustomTemplates_Server_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() throws IOException {
		this.graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases.graphqls");
	}

	/**
	 * This test will be executed for each concrete subclass of this class
	 * 
	 * @throws Exception
	 * 
	 * @throws MojoExecutionException
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
