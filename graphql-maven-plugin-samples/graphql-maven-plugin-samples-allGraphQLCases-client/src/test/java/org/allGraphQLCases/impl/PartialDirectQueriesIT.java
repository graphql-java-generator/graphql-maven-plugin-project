package org.allGraphQLCases.impl;

import org.allGraphQLCases.PartialQueries;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class PartialDirectQueriesIT extends AbstractIT {

	@Override
	protected PartialQueries getQueries() {
		return ctx.getBean(PartialDirectQueries.class);
	}

}
