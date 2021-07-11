package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Github_Client_SpringConfiguration;

@Tag("github")
class GithubPublicSchemaClientTest extends AbstractIntegrationTest {

	public GithubPublicSchemaClientTest() {
		super(Github_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) pluginConfiguration).separateUtilityClasses = true;
		graphqlTestHelper.checkSchemaStringProvider("github.schema.public.graphqls");
	}

	@Override
	protected void checkNbGeneratedClasses(int nbGeneratedClasses) {
		assertEquals(969, nbGeneratedClasses);
	}

}
