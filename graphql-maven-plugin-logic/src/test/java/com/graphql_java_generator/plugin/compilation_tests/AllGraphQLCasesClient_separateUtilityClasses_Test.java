package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration_separateUtilityClasses;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCases_Client_SpringConfiguration_separateUtilityClasses.class })
class AllGraphQLCasesClient_separateUtilityClasses_Test extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is to have its own Spring Configuration
	@BeforeEach
	public void setUp() {
		((PluginConfigurationTestHelper) pluginConfiguration).separateUtilityClasses = true;
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases.graphqls");
	}
}
