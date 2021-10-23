package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

class AllGraphQLCasesClientTest extends AbstractIntegrationTest {

	public AllGraphQLCasesClientTest() {
		super(AllGraphQLCases_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

}
