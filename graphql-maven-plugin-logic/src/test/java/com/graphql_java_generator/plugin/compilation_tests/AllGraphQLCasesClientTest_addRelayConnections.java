package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration_addRelayConnections;

class AllGraphQLCasesClientTest_addRelayConnections extends AbstractIntegrationTest {

	public AllGraphQLCasesClientTest_addRelayConnections() {
		super(AllGraphQLCases_Client_SpringConfiguration_addRelayConnections.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

	@Override
	protected void checkNbGeneratedClasses(int nbGeneratedClasses) {
		assertEquals(58, nbGeneratedClasses);
	}
}
