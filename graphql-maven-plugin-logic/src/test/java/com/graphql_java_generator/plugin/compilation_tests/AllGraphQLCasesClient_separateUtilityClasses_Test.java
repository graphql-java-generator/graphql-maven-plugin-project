package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration_separateUtilityClasses;

class AllGraphQLCasesClient_separateUtilityClasses_Test extends AbstractIntegrationTest {

	public AllGraphQLCasesClient_separateUtilityClasses_Test() {
		super(AllGraphQLCases_Client_SpringConfiguration_separateUtilityClasses.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) configuration).separateUtilityClasses = true;
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

}
