package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.QueryExecutorImpl_allGraphqlCases_Test;
import com.graphql_java_generator.domain.client.allGraphQLCases.GraphQLRequest;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_unionTest {

	Map<String, Object> params = new HashMap<>();

	@BeforeEach
	void setup() {
		// Default configuration for GraphQLRequest
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration("http://localhost"));
	}

	@Test
	void testBuild_withMissingFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		MyQueryType queryType = new MyQueryType("http://localhost");

		// No fragment for Human. But deserialization needs always the __typename to instanciate the proper class
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = queryType.getGraphQLRequest(""//
				+ "query{unionTest(human1:?human1,droid1:?droid1,human2:?human2,droid2:?droid2) {" //
				+ "    ... on Droid { id primaryFunction ... on Character {name(uppercase: ?uppercaseTrue) friends {name}}  } " //
				+ "  } "//
				+ "} " //
		);

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		assertEquals("{\"query\":\""//
				+ "query{unionTest{" //
				+ "... on AnyCharacter{__typename} " //
				+ "... on Droid{id primaryFunction ... on Character{name friends{name __typename} __typename}}" //
				+ "}}" //
				+ "\"}", //
				graphQLRequest.buildRequestAsString(params));

		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "query{unionTest{" //
				+ "... on AnyCharacter{__typename} " //
				+ "... on Droid{id primaryFunction ... on Character{name friends{name __typename} __typename}}" //
				+ "}}", //
				null, null);
	}
}
