package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Github_Client_SpringConfiguration;

@Disabled // Disabled because he test execution is long , and no error have ever been found by this test
@Tag("github")
class GithubPublicSchemaClientTest extends AbstractIntegrationTest {

	public GithubPublicSchemaClientTest() {
		super(Github_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) this.pluginConfiguration).separateUtilityClasses = true;
		this.graphqlTestHelper.checkSchemaStringProvider("github.schema.public.graphqls");
	}

}
