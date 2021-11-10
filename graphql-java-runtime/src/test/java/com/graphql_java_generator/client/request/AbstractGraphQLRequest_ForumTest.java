package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.QueryExecutorImpl_allGraphqlCases_Test;
import com.graphql_java_generator.client.request.InputParameter.InputParameterType;
import com.graphql_java_generator.domain.client.forum.Board;
import com.graphql_java_generator.domain.client.forum.GraphQLRequest;
import com.graphql_java_generator.domain.client.forum.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_ForumTest {

	QueryType queryType;
	Map<String, Object> params;

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new QueryType("http://localhost:graphql");

		params = new HashMap<>();
		params.put("memberName", "a member Name");
		params.put("sinceParam", new GregorianCalendar(1900, 10 - 1, 24).getTime());
	}

	@Test
	public void test_withQueryResponseDef_withHardCodedParameters_Forum()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		String queryResponseDef = "{id name publiclyAvailable "
				+ " topics{id date author{id name email type} nbPosts posts(memberName: \"Me!\", since: ?sinceParam) {date author{name email type}}}}";

		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = queryType.getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();

		// Verifications
		assertEquals(1, graphQLRequest.query.fields.size(), "nb queries");
		//
		QueryField boards = graphQLRequest.query.fields.get(0);
		assertEquals("boards", boards.name, "field name");
		assertEquals("boards", boards.getName(), "field name");
		assertEquals(Board.class, boards.clazz);
		assertNull(boards.alias);
		assertNull(boards.getAlias());
		//
		QueryField posts = boards.fields.get(3).fields.get(4);
		assertEquals("posts", posts.getName(), "Did we load the good field ?");
		assertEquals(2, posts.inputParameters.size());
		int i = 0;
		assertEquals("memberName", posts.inputParameters.get(i).name);
		assertTrue(posts.inputParameters.get(i).value instanceof RawGraphQLString);
		assertEquals("\"Me!\"", posts.inputParameters.get(i).value.toString());
		assertEquals(null, posts.inputParameters.get(i).bindParameterName);
		i += 1;
		assertEquals("since", posts.inputParameters.get(i).name);
		assertEquals(null, posts.inputParameters.get(i).value);
		assertEquals("sinceParam", posts.inputParameters.get(i).bindParameterName);
		assertEquals(InputParameterType.OPTIONAL, posts.inputParameters.get(i).type);
		//
		assertEquals("{\"query\":\"query{boards" //
				+ "{id name publiclyAvailable"//
				+ " topics{id date author{id name email type __typename} nbPosts"
				+ " posts(memberName:\\\"Me!\\\",since:\\\"1900-10-24\\\"){date author{name email type __typename} __typename} __typename} __typename}" //
				+ "}\"}", //
				graphQLRequest.buildRequestAsString(params));

		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "query{boards" //
				+ "{id name publiclyAvailable"//
				+ " topics{id date author{id name email type __typename} nbPosts"
				+ " posts(memberName:\"Me!\",since:\"1900-10-24\"){date author{name email type __typename} __typename} __typename} __typename}" //
				+ "}", //
				null, null);
	}

	@Test
	public void test_withQueryResponseDef_withBindVariableParameters_Forum()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation

		// Go, go, go
		String queryResponseDef = "{id name publiclyAvailable topics{id date author{id name email type} nbPosts "
				+ "posts(memberName: ?memberName, since: &sinceParam) {date author{name email type}}}}";
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = queryType.getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();

		// Verifications
		assertEquals(1, graphQLRequest.query.fields.size(), "nb queries");
		QueryField boards = graphQLRequest.query.fields.get(0);
		assertEquals("boards", boards.name);
		assertEquals("boards", boards.getName());
		assertEquals(Board.class, boards.clazz);
		assertEquals(Board.class, boards.getClazz());
		assertNull(boards.alias);
		assertNull(boards.getAlias());

		assertEquals(5, boards.fields.size(), " (with the added __typename field)");
		int i = 0;
		assertEquals("id", boards.fields.get(i++).name);
		assertEquals("name", boards.fields.get(i++).name);
		assertEquals("publiclyAvailable", boards.fields.get(i++).name);
		assertEquals("topics", boards.fields.get(i++).name);
		assertEquals("__typename", boards.fields.get(i++).name);

		QueryField topics = boards.fields.get(3);

		assertEquals("topics", topics.getName());
		assertEquals(6, topics.fields.size(), "with __typename");
		i = 0;
		assertEquals("id", topics.fields.get(i++).name);
		assertEquals("date", topics.fields.get(i++).name);
		QueryField author = topics.fields.get(i++);
		assertEquals("author", author.name);
		assertEquals("nbPosts", topics.fields.get(i++).name);
		QueryField posts = topics.fields.get(i++);
		assertEquals("posts", posts.name);
		assertEquals("__typename", topics.fields.get(i++).name);

		// Check of the input parameters

		//
		List<InputParameter> postsInputParameters = posts.inputParameters;
		assertEquals(2, postsInputParameters.size());
		i = 0;
		// First parameter is hard coded
		assertEquals("memberName", postsInputParameters.get(i).getName());
		assertEquals(null, postsInputParameters.get(i).getValue());
		assertEquals("\"a member Name\"", postsInputParameters.get(i).getValueForGraphqlQuery(false, params));
		assertEquals("memberName", postsInputParameters.get(i).bindParameterName);
		assertEquals(InputParameterType.OPTIONAL, postsInputParameters.get(i).type);
		i = 1;
		// The second parameter is a bind variable
		assertEquals("since", postsInputParameters.get(i).getName());
		assertEquals(null, postsInputParameters.get(i).getValue());
		assertEquals("\"1900-10-24\"", postsInputParameters.get(i).getValueForGraphqlQuery(false, params));
		assertEquals("sinceParam", postsInputParameters.get(i).bindParameterName);
		assertEquals(InputParameterType.MANDATORY, postsInputParameters.get(i).type);

		// Check of the built request
		assertEquals("{\"query\":\"query{boards"//
				+ "{id name publiclyAvailable topics{id date author{id name email type __typename} nbPosts "
				+ "posts(memberName:\\\"a member Name\\\",since:\\\"1900-10-24\\\"){date author{name email type __typename} __typename} __typename} __typename}"
				+ "}\"}", //
				graphQLRequest.buildRequestAsString(params));
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "query{boards"//
				+ "{id name publiclyAvailable topics{id date author{id name email type __typename} nbPosts "
				+ "posts(memberName:\"a member Name\",since:\"1900-10-24\"){date author{name email type __typename} __typename} __typename} __typename}}", //
				null, null);
	}

	@Test
	void testBuild_NoFields() throws GraphQLRequestPreparationException {
		// Go, go, go
		String queryResponseDef = "{topics}";
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = queryType.getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField boards = graphQLRequest.query.fields.get(0);
		assertEquals("boards", boards.name);
		assertEquals(2, boards.fields.size(), "all scalar fields (with the added __typename field)");
		//
		QueryField topics = boards.fields.get(0);
		assertEquals("topics", topics.name);
		assertEquals("__typename", boards.fields.get(1).name);
		//
		// topics should have automatically receive all its non scalar fields, as requested subfields
		assertEquals(7, topics.fields.size());
		int i = 0;
		assertEquals("id", topics.fields.get(i++).name);
		assertEquals("date", topics.fields.get(i++).name);
		assertEquals("publiclyAvailable", topics.fields.get(i++).name);
		assertEquals("nbPosts", topics.fields.get(i++).name);
		assertEquals("title", topics.fields.get(i++).name);
		assertEquals("content", topics.fields.get(i++).name);
		assertEquals("__typename", topics.fields.get(i++).name);
	}

	@Test
	void testBuild_fullQueryWithQueryName()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest("query aQueryName {boards{topics{id}}}");
		Map<String, Object> params = new HashMap<>();

		// Verification
		assertEquals("aQueryName", graphQLRequest.getRequestName());
		assertEquals("{\"query\":\"query aQueryName{boards{topics{id __typename} __typename}}\"}",
				graphQLRequest.buildRequestAsString(params));
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "query aQueryName{boards{topics{id __typename} __typename}}", //
				null, null);
	}

}
