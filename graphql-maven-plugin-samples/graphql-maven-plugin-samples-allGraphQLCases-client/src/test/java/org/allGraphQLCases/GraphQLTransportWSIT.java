package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.allGraphQLCases.GraphQLTransportWSIT.GraphQLTransportWSSpringConfiguration;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.graphql_java_generator.client.OAuthTokenExtractor;
import com.graphql_java_generator.client.RequestExecution;
import com.graphql_java_generator.client.RequestExecutionGraphQLTransportWSImpl;

/**
 * This class allows to test the graphql-transport-ws protocol and its associated request executor:
 * {@link RequestExecutionGraphQLTransportWSImpl}
 * 
 * @author etienne-sf
 */
@SpringBootTest(classes = GraphQLTransportWSSpringConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class GraphQLTransportWSIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases myQuery;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;
	@Autowired
	RequestExecution requestExecutor;

	@Configuration
	@Import(SpringTestConfig.class)
	public static class GraphQLTransportWSSpringConfiguration {
		@Bean
		@Primary
		public RequestExecution requestExecutionAllGraphQLCases(String graphqlEndpointAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("graphqlSubscriptionEndpointAllGraphQLCases") String graphqlSubscriptionEndpointAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("webClientAllGraphQLCases") WebClient webClientAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("webSocketClientAllGraphQLCases") WebSocketClient webSocketClientAllGraphQLCases,
				@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases,
				@Autowired(required = false) @Qualifier("oAuthTokenExtractorAllGraphQLCases") OAuthTokenExtractor oAuthTokenExtractorAllGraphQLCases) {
			// Returns the Request executor that can execute queries, mutations and subscriptions according to the
			// graphql-transport-ws protocol
			return new RequestExecutionGraphQLTransportWSImpl(graphqlEndpointAllGraphQLCases,
					graphqlSubscriptionEndpointAllGraphQLCases, webClientAllGraphQLCases,
					webSocketClientAllGraphQLCases, serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases,
					oAuthTokenExtractorAllGraphQLCases);
		}
	}

	@BeforeEach
	void setup() {
		assertTrue(requestExecutor instanceof RequestExecutionGraphQLTransportWSImpl,
				"requestExecutor should be an instance of RequestExecutionGraphQLTransportWSImpl");
	}

	@Test
	void testQueryWithoutGraphQLVariables() {
		fail("not yet implemented");
	}

	@Test
	void testQueryWithGraphQLVariables() {
		fail("not yet implemented");
	}

	@Test
	void testMutationWithoutGraphQLVariables() {
		fail("not yet implemented");
	}

	@Test
	void testMutationWithGraphQLVariables() {
		fail("not yet implemented");
	}
}
