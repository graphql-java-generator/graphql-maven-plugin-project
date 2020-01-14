package org.allGraphQLCases.graphql;

import org.allGraphQLCases.Main;
import org.allGraphQLCases.client.MyQueryType;
import org.junit.jupiter.api.BeforeEach;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
class WithBuilderIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {

		// For some tests, we need to execute additional queries
		queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Creation of the instance, against which we'll execute the JUnit tests
		queries = new DirectQueries(Main.GRAPHQL_ENDPOINT);
	}

}
