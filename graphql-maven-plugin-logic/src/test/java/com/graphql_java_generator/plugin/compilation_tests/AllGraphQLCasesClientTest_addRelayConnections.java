package com.graphql_java_generator.plugin.compilation_tests;

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

}
