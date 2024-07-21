package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Shopify_Client_SpringConfiguration;

@Disabled // Disabled because he test execution is long , and no error have ever been found by this test
@Tag("shopify")
class ShopifyClientTest extends AbstractIntegrationTest {

	public ShopifyClientTest() {
		super(Shopify_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		((GraphQLConfigurationTestHelper) this.configuration).separateUtilityClasses = true;
		this.graphqlTestHelper.checkSchemaStringProvider("shopify.graphqls");
	}

}
