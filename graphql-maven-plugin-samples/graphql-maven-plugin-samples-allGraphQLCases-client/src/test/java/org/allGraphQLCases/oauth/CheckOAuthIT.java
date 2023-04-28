/**
 * 
 */
package org.allGraphQLCases.oauth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.allGraphQLCases.client.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.graphql.client.GraphQlTransportException;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 */
@Order(Integer.MIN_VALUE) // This test must run last, to avoid breaking the Spring context for other tests. See the
							// src/test/resources/junit-platform.properties file for JUnit class ordering configuration
public class CheckOAuthIT {
	MyQueryTypeExecutorAllGraphQLCases queryType;
	AnotherMutationTypeExecutorAllGraphQLCases mutation;

	/**
	 * Test of list that contain list, when sending request and receiving response
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void testThatOAuth2IsActive() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Preparation
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				SpringTestConfigWithoutOAuth.class);

		// For some tests, we need to execute additional partialQueries
		queryType = ctx.getBean(MyQueryTypeExecutorAllGraphQLCases.class);
		assertNotNull(queryType);
		mutation = ctx.getBean(AnotherMutationTypeExecutorAllGraphQLCases.class);
		assertNotNull(mutation);

		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = queryType
				.getWithListOfListGraphQLRequest("{matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();

		// Go, go, go
		GraphQlTransportException e = assertThrows(GraphQlTransportException.class,
				() -> queryType.withListOfList(GraphQLRequestAllGraphQLCases, matrixSrc));

		// Verification
		assertTrue(e.getMessage().contains("401 Unauthorized"), "The OAuth2 use control must be active on server side");

		ctx.close();
	}
}
