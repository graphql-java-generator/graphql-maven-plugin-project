package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.domain.forum.GraphQLRequest;
import com.graphql_java_generator.client.domain.forum.Post;
import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.exception.GraphQLResponseParseException;

/**
 * 
 * @author EtienneSF
 */
@Execution(ExecutionMode.CONCURRENT)
class QueryExecutorImpl_Forum_Test {

	QueryExecutorImpl queryExecutorImpl;

	@BeforeEach
	void setUp() throws Exception {
		queryExecutorImpl = new QueryExecutorImpl("http://localhost:8180/graphql");
	}

	@Test
	void parseResponse_OK_Topics_withCustomScalar()
			throws GraphQLRequestPreparationException, GraphQLResponseParseException, IOException {
		// Preparation
		ObjectResponse createPostResponse = new Builder(GraphQLRequest.class, "createPost", RequestType.mutation)
				.withQueryResponseDef("{id date author{id} title content publiclyAvailable}").build();
		String rawResponse = "{\"data\":{\"post\":{\"id\":\"d87c5c05-7cca-4302-adc8-627a282b1f1b\","
				+ "\"date\":\"2009-11-21\"," + "\"title\":\"The good title for a post\","
				+ "\"content\":\"Some other content\"," + "\"publiclyAvailable\":false,"
				+ "\"author\":{\"id\":\"00000000-0000-0000-0000-000000000012\"}}}}";

		// Go, go, go
		Post post = parseResponseForForumSchema(rawResponse, createPostResponse, Post.class);

		// Verification
		@SuppressWarnings("deprecation")
		Date date = new Date(2009 - 1900, 11 - 1, 21);// Years starts at 1900. Month is between 0 and 11
		assertEquals(date, post.getDate(), "The Custom Scalar date should have been properly deserialized");
	}

	<T> T parseResponseForForumSchema(String rawResponse, ObjectResponse createPostResponse, Class<T> valueType)
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

	@Test
	void test_buildRequest_withFieldParameters_bindVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		ObjectResponse objectResponse = new Builder(GraphQLRequest.class, "boards", RequestType.query)
				.withQueryResponseDef(
						"{id name publiclyAvailable topics(since: \"2018-12-20\") {id date author{id name email type} nbPosts posts{date author{name email type}}}}")
				.build();

		// Go, go, go
		String request = objectResponse.buildRequest(null);

		// Verification
		assertEquals(
				"{\"query\":\"query{boards{id name publiclyAvailable topics(since:\\\"2018-12-20\\\"){id date author{id name email type __typename} nbPosts posts{date author{name email type __typename} __typename} __typename} __typename}}\",\"variables\":null,\"operationName\":null}",
				request);
	}

	@Test
	void test_buildRequest_withFieldParameters_hardCoded()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		ObjectResponse objectResponse = new Builder(GraphQLRequest.class, "boards", RequestType.query)
				.withQueryResponseDef(
						"{id name publiclyAvailable topics(since: \"2019-12-21\") {id date author{id name email type} nbPosts posts{date author{name email type}}}}")
				.build();
		Map<String, Object> parameters = new HashMap<>();

		// Go, go, go
		String request = objectResponse.buildRequest(parameters);

		// Verification
		assertEquals(
				"{\"query\":\"query{boards{id name publiclyAvailable topics(since:\\\"2019-12-21\\\"){id date author{id name email type __typename} nbPosts posts{date author{name email type __typename} __typename} __typename} __typename}}\",\"variables\":null,\"operationName\":null}",
				request);
	}
}
