package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Shopify_Client_SpringConfiguration;

@Tag("shopify")
class ShopifyClientTest extends AbstractIntegrationTest {

	public ShopifyClientTest() {
		super(Shopify_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) pluginConfiguration).separateUtilityClasses = true;
		graphqlTestHelper.checkSchemaStringProvider("shopify.graphqls");
	}

	@Override
	protected void checkNbGeneratedClasses(int nbGeneratedClasses) {
		assertEquals(835, nbGeneratedClasses);
	}

}
