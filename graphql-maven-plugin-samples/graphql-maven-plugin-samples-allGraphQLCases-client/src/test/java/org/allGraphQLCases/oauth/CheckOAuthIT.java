/**
 * 
 */
package org.allGraphQLCases.oauth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class CheckOAuthIT {
	MyQueryTypeExecutorAllGraphQLCases queryType;
	AnotherMutationTypeExecutorAllGraphQLCases mutation;

	ApplicationContext ctx;

	@BeforeEach
	void setup() {
		ctx = new AnnotationConfigApplicationContext(SpringTestConfigWithoutOAuth.class);

		// For some tests, we need to execute additional partialQueries
		queryType = ctx.getBean(MyQueryTypeExecutorAllGraphQLCases.class);
		assertNotNull(queryType);
		mutation = ctx.getBean(AnotherMutationTypeExecutorAllGraphQLCases.class);
		assertNotNull(mutation);
	}

	/**
	 * Test of list that contain list, when sending request and receiving response
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testThatOAuth2IsActive() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = queryType.getWithListOfListGraphQLRequest("{matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();

		// Go, go, go
		WebClientResponseException.Unauthorized e = assertThrows(WebClientResponseException.Unauthorized.class,
				() -> queryType.withListOfList(graphQLRequest, matrixSrc));

		// Verification
		assertTrue(e.getMessage().contains("401 Unauthorized"), "The OAuth2 use control must be active on server side");
	}
}
