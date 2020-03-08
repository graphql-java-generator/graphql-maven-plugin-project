package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.exception.GraphQLResponseParseException;

/**
 * 
 * @author EtienneSF
 */
class QueryExecutorImpl_allGraphqlCases_Test {

	QueryExecutorImpl queryExecutorImpl;

	@BeforeEach
	void setUp() throws Exception {
		queryExecutorImpl = new QueryExecutorImpl("http://localhost:8180/graphql");
	}

	@Test
	void buildRequest_withEnum_withDirectives_direct()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		MyQueryType myQueryType = new MyQueryType("http://localhost");
		String query = "{\n"
				+ "    id     @anotherTestDirective    @testDirective  ( value: \"id1 value\", anotherValue:\" something else for id1 \")\n"
				+ "    name\n" //
				+ "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value3\", anotherValue:\"something_else3\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "}\n";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("myQueryTypeWithEnumEpisode", Episode.JEDI);
		ObjectResponse objectResponse = myQueryType.getWithEnumResponseBuilder().withQueryResponseDef(query).build();

		// Go, go, go
		String request = queryExecutorImpl.buildRequest("query", objectResponse, parameters);

		// Verification
		assertEquals("{\"query\":\"query{withEnum(episode:JEDI)"//
				+ "{id @anotherTestDirective @testDirective(value:\\\"id1 value\\\",anotherValue:\\\" something else for id1 \\\") "
				+ "name " //
				+ "appearsIn @testDirective(value:\\\"a value2\\\",anotherValue:\\\"something else2\\\") "
				+ "__typename "
				+ "friends{id @anotherTestDirective name @testDirective(value:\\\"a value3\\\",anotherValue:\\\"something_else3\\\") @anotherTestDirective "//
				+ "__typename}}}\"" //
				+ ",\"variables\":null,\"operationName\":null}", request);
	}

	@Test
	void buildRequest_withEnum_withDirectives_prepared() throws GraphQLRequestExecutionException {
		// Preparation
		String query = "{\n"
				+ "    id  @anotherTestDirective  @testDirective(value: \"a value\", anotherValue:\"something else\")\n"
				+ "    name\n" + "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value2\", anotherValue:\"something else2\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "}\n";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("episode", Episode.JEDI);
		ObjectResponse objectResponse = null;

		// Go, go, go
		String request = queryExecutorImpl.buildRequest("query", objectResponse, parameters);

		// Verification
		assertEquals(
				"{\"query\":\"query{withEnum(episode:JEDI) @testDirective(value:\"a value\",anotherValue:\"something else\")"
						+ "{id @anotherTestDirective @testDirective(value:\"value\", anotherValue:\"something else\") "
						+ "name appearsIn @testDirective(value:\"a value2\",anotherValue:\"something else2\") "
						+ "friends{id @anotherTestDirective name @testDirective(value:\"a value2\", anotherValue:\"something else2\") @anotherTestDirective}}}\""
						+ ",\"variables\":null,\"operationName\":null}",
				request);

		fail("not implemented");
	}

	@Test
	void buildRequest_withEnum_withDirectives_withBuilder() throws GraphQLRequestExecutionException {
		// Preparation
		String query = "{\n"
				+ "    id  @anotherTestDirective  @testDirective(value: \"a value\", anotherValue:\"something else\")\n"
				+ "    name\n" + "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value2\", anotherValue:\"something else2\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "}\n";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("episode", Episode.JEDI);
		ObjectResponse objectResponse = null;

		// Go, go, go
		String request = queryExecutorImpl.buildRequest("query", objectResponse, parameters);

		// Verification
		assertEquals(
				"{\"query\":\"query{withEnum(episode:JEDI) @testDirective(value:\"a value\",anotherValue:\"something else\")"
						+ "{id @anotherTestDirective @testDirective(value:\"value\", anotherValue:\"something else\") "
						+ "name appearsIn @testDirective(value:\"a value2\",anotherValue:\"something else2\") "
						+ "friends{id @anotherTestDirective name @testDirective(value:\"a value2\", anotherValue:\"something else2\") @anotherTestDirective}}}\""
						+ ",\"variables\":null,\"operationName\":null}",
				request);

		fail("not implemented");
	}

	@Test
	void buildRequest_query_withEnum_withDirectives_direct() throws GraphQLRequestExecutionException {
		// Preparation
		String query = "{\n"
				+ "  withEnum(episode: JEDI  ) @testDirective(value: \"a value\", anotherValue:\"something else\") {\n"
				+ "    id  @anotherTestDirective  @testDirective(value: \"a value\", anotherValue:\"something else\")\n"
				+ "    name\n" + "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value2\", anotherValue:\"something else2\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "  }\n" //
				+ "}\n";

		fail("not implemented");
	}

	@Test
	void buildRequest_query_withEnum_withDirectives_prepared() throws GraphQLRequestExecutionException {
		// Preparation
		String query = "{\n"
				+ "  withEnum(episode: JEDI  ) @testDirective(value: \"a value\", anotherValue:\"something else\") {\n"
				+ "    id  @anotherTestDirective  @testDirective(value: \"a value\", anotherValue:\"something else\")\n"
				+ "    name\n" + "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value2\", anotherValue:\"something else2\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "  }\n" //
				+ "}\n";

		fail("not implemented");
	}

	@Test
	void buildRequest_query_withEnum_withDirectives_withBuilder() throws GraphQLRequestExecutionException {
		// Preparation
		String query = "{\n"
				+ "  withEnum(episode: JEDI  ) @testDirective(value: \"a value\", anotherValue:\"something else\") {\n"
				+ "    id  @anotherTestDirective  @testDirective(value: \"a value\", anotherValue:\"something else\")\n"
				+ "    name\n" + "    appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")\n"
				+ "    friends {\n"//
				+ "      id @anotherTestDirective\n"
				+ "      name @testDirective(value: \"a value2\", anotherValue:\"something else2\") @anotherTestDirective\n"
				+ "    }\n"//
				+ "  }\n" //
				+ "}\n";

		fail("not implemented");
	}

	<T> T parseResponse(String rawResponse, ObjectResponse createPostResponse, Class<T> valueType)
			throws IOException, GraphQLResponseParseException {

		// Let's read this response with Jackson
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(rawResponse);

		// The main node should be unique, named data, and be a container
		if (node.size() != 1)
			throw new GraphQLResponseParseException(
					"The response should contain one root element, but it contains " + node.size() + " elements");

		JsonNode data = node.get("data");
		if (data == null)
			throw new GraphQLResponseParseException("Could not retrieve the 'data' node");

		JsonNode post = data.get("post");
		if (post == null)
			throw new GraphQLResponseParseException("Could not retrieve the 'post' node");

		return mapper.treeToValue(post, valueType);
	}
}
