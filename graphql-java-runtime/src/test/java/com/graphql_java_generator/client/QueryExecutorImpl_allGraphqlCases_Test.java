package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.domain.allGraphQLCases.Character;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.client.request.Builder;
import com.graphql_java_generator.client.request.InputParameter;
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
	void buildRequest_withEnum_withDirectives_prepared()
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
	void buildRequest_withEnum_withDirectives_withBuilder()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		MyQueryType myQueryType = new MyQueryType("http://localhost");
		// {
		// id @anotherTestDirective @testDirective(value:"id1 value",anotherValue:" something else for id1 ")
		// name
		// appearsIn @testDirective(value: \"a value2\", anotherValue:\"something else2\")
		// friends {
		// id @anotherTestDirective
		// name @testDirective(value: \"a value3\", anotherValue:\"something_else3\") @anotherTestDirective
		// }
		// }
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("myQueryTypeWithEnumEpisode", Episode.JEDI);
		//
		Directive idDirective01 = new Directive("anotherTestDirective");
		Directive idDirective02 = new Directive("testDirective",
				InputParameter.newHardCodedParameter("value", "id1 value"),
				InputParameter.newHardCodedParameter("anotherValue", " something else for id1 "));
		//
		Directive appearsInDirective = new Directive("testDirective",
				InputParameter.newHardCodedParameter("value", "a value2"),
				InputParameter.newHardCodedParameter("anotherValue", "something else2"));
		//
		Directive idDirective11 = new Directive("testDirective",
				InputParameter.newHardCodedParameter("value", "a value3"),
				InputParameter.newHardCodedParameter("anotherValue", "something_else3"));
		Directive idDirective12 = new Directive("anotherTestDirective");
		//
		ObjectResponse friendsResponse = new Builder(Character.class, "friends")//
				.withField("id", null, null, Arrays.asList(new Directive("anotherTestDirective")))//
				.withField("name", null, null, Arrays.asList(idDirective11, idDirective12))//
				.build();
		ObjectResponse objectResponse = myQueryType.getWithEnumResponseBuilder()
				.withField("id", null, null, Arrays.asList(idDirective01, idDirective02))//
				.withField("name")//
				.withField("appearsIn", null, null, Arrays.asList(appearsInDirective))//
				.withSubObject(friendsResponse)//
				.build();

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
	void buildRequest_query_withEnum_withDirectives_prepared()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		MyQueryType myQueryType = new MyQueryType("http://localhost");
		String query = "{\n" + //
				"  withoutParameters @include(if: true) @anotherTestDirective {\n" + //
				"    id\n" + //
				"    name @include(if: false)\n" + //
				"    friends {\n" + //
				"      name @anotherTestDirective @testDirective(value: \"no value\")\n" + //
				"    }\n" + //
				"  }\n" + //
				"}";//
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("myQueryTypeWithEnumEpisode", Episode.JEDI);
		ObjectResponse objectResponse = myQueryType.getResponseBuilder().withQueryResponseDef(query).build();

		// Go, go, go
		String request = queryExecutorImpl.buildRequest("query", objectResponse, parameters);

		// Verification
		assertEquals("{\"query\":\"query{" + //
				"withoutParameters @include(if:true) @anotherTestDirective{" + //
				"id name @include(if:false) __typename " + //
				"friends{name @anotherTestDirective @testDirective(value:\\\"no value\\\") __typename}" + //
				"}" + //
				"}\"" + //
				",\"variables\":null,\"operationName\":null}", request);
	}

	@Disabled
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

	@Disabled
	@Test
	void buildRequest_multipleQueries_withEnum_withDirectives_prepared()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		MyQueryType myQueryType = new MyQueryType("http://localhost");
		String query = "{\n" + //
				"  withoutParameters @include(if: true) @anotherTestDirective {\n" + //
				"    id\n" + //
				"    name @include(if: false)\n" + //
				"    friends {\n" + //
				"      name @anotherTestDirective @testDirective(value: \"no value\")\n" + //
				"    }\n" + //
				"  }\n" + //
				"  withFriendsName: withoutParameters @testDirective(value: \" the value for withFriendssName\", anotherValue: \" no idea of this one\") {\n"
				+ //
				"    id\n" + //
				"    name @skip(if: true)\n" + //
				"    friends {\n" + //
				"      name @anotherTestDirective\n" + //
				"    }\n" + //
				"  }\n" + //
				"}";//
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("myQueryTypeWithEnumEpisode", Episode.JEDI);
		ObjectResponse objectResponse = myQueryType.getResponseBuilder().withQueryResponseDef(query).build();

		// Go, go, go
		String request = queryExecutorImpl.buildRequest("query", objectResponse, parameters);

		// Verification
		assertEquals("{\"query\":\"query{" + //
				"withoutParameters @include(if:true) @anotherTestDirective{" + //
				"id name @include(if:false) __typename " + //
				"friends{name @anotherTestDirective @testDirective(value:\"no value\")}" + //
				"}" + //
				"withFriendsName:withoutParameters @testDirective(value:\" the value for withFriendssName\",anotherValue:\" no idea of this one\"){"
				+ "id name @skip(if:true) __typename" + //
				"friends{name @anotherTestDirective __typename}" + //
				"}" + //
				"}\"" //
				+ ",\"variables\":null,\"operationName\":null}", request);
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
