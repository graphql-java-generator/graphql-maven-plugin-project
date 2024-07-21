package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Shopify_Server_SpringConfiguration;

@Disabled // Disabled as the generated code generates a Stack Overflow in the java compiler!
@Tag("shopify")
class ShopifyServerTest extends AbstractIntegrationTest {

	public ShopifyServerTest() {
		super(Shopify_Server_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) configuration).separateUtilityClasses = true;
		graphqlTestHelper.checkSchemaStringProvider("shopify.graphqls");
	}

}
