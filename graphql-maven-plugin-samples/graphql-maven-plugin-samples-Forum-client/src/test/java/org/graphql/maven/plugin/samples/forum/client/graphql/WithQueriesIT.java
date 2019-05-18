package org.graphql.maven.plugin.samples.forum.client.graphql;

import org.junit.jupiter.api.BeforeEach;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
class WithQueriesIT extends AbstractTest {

	@BeforeEach
	void setUp() throws Exception {
		queries = new WithQueries();
	}

}
