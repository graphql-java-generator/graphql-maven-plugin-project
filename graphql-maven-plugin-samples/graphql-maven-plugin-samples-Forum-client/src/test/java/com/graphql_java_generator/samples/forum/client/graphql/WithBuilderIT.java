package com.graphql_java_generator.samples.forum.client.graphql;

import org.junit.jupiter.api.BeforeEach;

import com.graphql_java_generator.samples.forum.client.graphql.WithBuilder;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
class WithBuilderIT extends AbstractTest {

	@BeforeEach
	void setUp() throws Exception {
		queries = new WithBuilder();
	}

}
