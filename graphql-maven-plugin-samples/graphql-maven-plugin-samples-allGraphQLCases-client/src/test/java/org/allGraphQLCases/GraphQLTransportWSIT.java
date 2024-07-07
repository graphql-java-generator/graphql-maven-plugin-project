package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.allGraphQLCases.GraphQLTransportWSIT.GraphQLTransportWSSpringConfiguration;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.graphql_java_generator.client.OAuthTokenExtractor;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class allows to test the graphql-transport-ws protocol and its associated request executor:
 * {@link RequestExecutionGraphQLTransportWSImpl}
 * 
 * @author etienne-sf
 */
@SpringBootTest(classes = GraphQLTransportWSSpringConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class GraphQLTransportWSIT {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLTransportWSIT.class);

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases myQuery;

	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;

	@Autowired
	@Qualifier("graphQlHttpClientAllGraphQLCases")
	GraphQlClient graphQlHttpClientAllGraphQLCases;

	@Bean
	@ConditionalOnMissingBean
	OAuthTokenExtractor oAuthTokenExtractor(
			@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {
		if (serverOAuth2AuthorizedClientExchangeFilterFunction == null)
			return null;
		else
			return new OAuthTokenExtractor(serverOAuth2AuthorizedClientExchangeFilterFunction);
	}

	@Configuration
	@Import(SpringTestConfig.class)
	public static class GraphQLTransportWSSpringConfiguration {
		@Bean
		@Primary
		public GraphQlClient graphQlHttpClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("graphqlSubscriptionEndpointAllGraphQLCases") String graphqlSubscriptionEndpointAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("webClientAllGraphQLCases") WebClient webClientAllGraphQLCases, //
				@Autowired(required = false) @Qualifier("webSocketClientAllGraphQLCases") WebSocketClient webSocketClientAllGraphQLCases,
				@Autowired(required = false) @Qualifier("OAuthTokenExtractor") OAuthTokenExtractor oAuthTokenExtractorAllGraphQLCases) {

			Builder<?> ret = WebSocketGraphQlClient//
					.builder(graphqlSubscriptionEndpointAllGraphQLCases, new ReactorNettyWebSocketClient());

			if (oAuthTokenExtractorAllGraphQLCases != null) {
				ret.headers(new Consumer<HttpHeaders>() {
					@Override
					public void accept(HttpHeaders headers) {
						String authorizationHeaderValue = oAuthTokenExtractorAllGraphQLCases
								.getAuthorizationHeaderValue();
						logger.debug("Got this OAuth token (authorization header value): {}", authorizationHeaderValue);
						headers.add(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME, authorizationHeaderValue);
					}
				});
			}

			// Returns the Spring GraphQL client that executes the request according to the graphql-transport-ws
			// protocol
			return ret.build();
		}
	}

	@BeforeEach
	void setup() {
		assertTrue(graphQlHttpClientAllGraphQLCases instanceof WebSocketGraphQlClient,
				"The graphQlClient should be an instance of WebSocketGraphQlClient");
	}

	@Test
	void testQueryWithError() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation

		// Go, go, go
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> myQuery.exec("query {error(errorLabel:\"This is an expected error\") {  }}"));

		// Verifications
		assertTrue(e.getMessage().contains("This is an expected error"), "the error message is: " + e.getMessage());
	}

	@Test
	void testQueryWithoutGraphQLVariables()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = myQuery
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = myQuery.allFieldCases(GraphQLRequestAllGraphQLCases, null, "inputs", inputs);

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	void testQueryWithGraphQLVariables() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);

		// Go, go, go
		CTP_MyQueryType_CTS resp = myQuery.exec(
				"query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", //
				"matrixParam", matrix);

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Test
	void testMutationWithoutGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		String request = "mutation mut1 {"//
				+ "createHuman (human:  {name: \"a name with a string that contains a \\\", two { { and a } \", friends: [], appearsIn: [JEDI,NEWHOPE]} )"//
				+ "@testDirective(value:?value, anotherValue:?anotherValue, anArray  : [  \"a string that contains [ [ and ] that should be ignored\" ,  \"another string\" ] , \r\n"
				+ "anObject:{    name: \"a name\" , appearsIn:[],friends : [{name:\"subname\",appearsIn:[],type:\"\"}],type:\"type\"})   {id name appearsIn friends {id name}}}";
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = new GraphQLRequestAllGraphQLCases(request);

		// Go, go, go
		CTP_Human_CTS human = mutationType.execWithBindValues(GraphQLRequestAllGraphQLCases, null).getCreateHuman();

		// Verifications
		assertEquals("a name with a string that contains a \", two { { and a } ", human.getName());
	}

	@Test
	void testMutationWithGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases directiveOnQuery = mutationType
				.getGraphQLRequest("query namedQuery($uppercase :\n" //
						+ "Boolean, \n\r"//
						+ " $Value :   String ! , $anotherValue:String) {directiveOnQuery (uppercase: $uppercase) @testDirective(value:$Value, anotherValue:$anotherValue)}");
		Map<String, Object> params = new HashMap<>();
		params.put("uppercase", true);
		params.put("anotherValue", "another value with an antislash: \\");
		params.put("Value", "a first \"value\"");

		// Go, go, go
		CTP_MyQueryType_CTS resp = directiveOnQuery.execQuery(params);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("A FIRST \"VALUE\"", ret.get(0));
		assertEquals("ANOTHER VALUE WITH AN ANTISLASH: \\", ret.get(1));
	}
}
