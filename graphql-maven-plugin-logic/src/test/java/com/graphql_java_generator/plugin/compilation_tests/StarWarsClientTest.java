package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import graphql.mavenplugin_notscannedbyspring.StarWars_Client_SpringConfiguration;

@Tag("starwars")
class StarWarsClientTest extends AbstractIntegrationTest {

	public StarWarsClientTest() {
		super(StarWars_Client_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("starWarsSchema.graphqls");
	}

	@Override
	protected void checkNbGeneratedClasses(int nbGeneratedClasses) {
		assertEquals(31, nbGeneratedClasses);
	}

}
