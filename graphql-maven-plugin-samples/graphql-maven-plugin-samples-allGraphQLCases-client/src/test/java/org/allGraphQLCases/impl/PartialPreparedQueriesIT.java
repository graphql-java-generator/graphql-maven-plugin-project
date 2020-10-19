package org.allGraphQLCases.impl;

import org.allGraphQLCases.Main;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class PartialPreparedQueriesIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {

		// For some tests, we need to execute additional partialQueries
		queryType = new MyQueryTypeExecutor(Main.GRAPHQL_ENDPOINT);

		// Creation of the instance, against which we'll execute the JUnit tests
		partialQueries = new PartialPreparedQueries(Main.GRAPHQL_ENDPOINT);
	}

}
