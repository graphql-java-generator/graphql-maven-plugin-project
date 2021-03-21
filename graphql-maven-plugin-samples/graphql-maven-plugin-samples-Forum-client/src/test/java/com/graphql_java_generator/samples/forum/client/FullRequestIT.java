/**
 * 
 */
package com.graphql_java_generator.samples.forum.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;

/**
 * This class is both samples and integration tests for Full GraphQL request, that contains GraphQL fragments.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class FullRequestIT {

	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	static GraphQLRequest boardsRequest;

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@BeforeAll
	static void setupAll() throws GraphQLRequestPreparationException {
		// We have one GraphQL endpoint. So we use the static configuration.
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration(GRAPHQL_ENDPOINT_URL));

		// Let's build once the request, and use it for each further execution
		boardsRequest = new GraphQLRequest("query{boards{id name topics {id}}}");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void extensionsResponseField()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, JsonProcessingException {

		// Go, go, go
		QueryType resp = boardsRequest.execQuery();

		// Verifications
		// The extensions field contains a Human instance, for the key "aValueToTestTheExtensionsField".
		// Check the org.allGraphQLCases.server.extensions.CustomBeans (creation of the customGraphQL Spring bean)
		assertNotNull(resp);
		assertNotNull(resp.getExtensions());
		assertNotNull(resp.getExtensionsAsMap());
		assertNotNull(resp.getExtensionsAsMap().get("aValueToTestTheExtensionsField"));
		ExtensionValue value = resp.getExtensionsField("aValueToTestTheExtensionsField", ExtensionValue.class);
		assertEquals("The name", value.name);
		assertEquals("The forname", value.forname);
	}
}
