package com.graphql_java_generator.mavenplugin.samples.simple.client.graphql;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.generated.graphql.util.QueryTypeExecutor;
import com.graphql_java_generator.mavenplugin.samples.SpringTestConfig;
import com.graphql_java_generator.samples.simple.client.graphql.PartialDirectRequests;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
@TestPropertySource("classpath:application.properties")
@Execution(ExecutionMode.CONCURRENT)
class PartialPreparedRequestsIT extends AbstractIT {

	@Autowired
	// Spring will inject the necessary beans into this constructor
	public PartialPreparedRequestsIT(PartialDirectRequests queries, QueryTypeExecutor queryType) {
		super.queries = queries;
		this.queryType = queryType;
	}

}
