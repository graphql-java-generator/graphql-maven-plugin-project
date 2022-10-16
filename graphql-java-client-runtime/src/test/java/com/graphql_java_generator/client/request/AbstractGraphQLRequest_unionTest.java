package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_unionTest {

	Map<String, Object> params = new HashMap<>();

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.getBean(anyString(), any(Class.class))).thenReturn(null);
		SpringContextBean.setApplicationContext(applicationContext);
	}

	@Test
	void testBuild_withMissingFragments()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		MyQueryType queryType = new MyQueryType();

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

		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), ""//
				+ "query{unionTest{" //
				+ "... on AnyCharacter{__typename} " //
				+ "... on Droid{id primaryFunction ... on Character{name friends{name __typename} __typename}}" //
				+ "}}", //
				null, null);
	}
}
