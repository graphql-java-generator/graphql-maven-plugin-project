package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_ServerPojo_SpringConfiguration;

class AllGraphQLCasesServerPojoTest extends AbstractIntegrationTest {

	public AllGraphQLCasesServerPojoTest() {
		super(AllGraphQLCases_ServerPojo_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

}
