package com.graphql_java_generator.samples.forum.client.graphql;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.graphqlrepository.GraphQLRepositoryInvocationHandler;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionExecutor;

/**
 * Integration tests for GraphQL Repository, in Spring mode<BR/>
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL Forum server, see the
 * pom.xml file for details.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class GraphQLRepositoryPartialRequestsNonSpringIT extends AbstractIT {

	static String ENDPOINT = "http://localhost:8182/graphql";

	@BeforeEach
	void setUp() throws Exception {
		GraphQLRepositoryInvocationHandler<GraphQLRepositoryPartialRequests> invocationHandler = new GraphQLRepositoryInvocationHandler<GraphQLRepositoryPartialRequests>(
				GraphQLRepositoryPartialRequests.class, new QueryExecutor(ENDPOINT),
				new MutationExecutor(ENDPOINT), new SubscriptionExecutor(ENDPOINT));
		queries = (GraphQLRepositoryPartialRequests) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { GraphQLRepositoryPartialRequests.class }, invocationHandler);
	}

}
