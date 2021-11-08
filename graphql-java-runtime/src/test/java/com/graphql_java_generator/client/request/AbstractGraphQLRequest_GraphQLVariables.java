package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.QueryExecutorImpl_allGraphqlCases_Test;
import com.graphql_java_generator.domain.client.forum.CustomScalarRegistryInitializer;
import com.graphql_java_generator.domain.client.forum.GraphQLRequest;
import com.graphql_java_generator.domain.client.forum.MemberType;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.forum.QueryType;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_GraphQLVariables {

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
	void testBuild_withGraphQLVariables() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(
				"mutation crPst  ($post: PostInput!, $anIntParam: Int){createPost(post: $post){id date author{id}}}");
		CustomScalarRegistryInitializer.initCustomScalarRegistry();
		TopicPostInput topicPostInput = TopicPostInput.builder().withAuthorId("12")
				.withDate(new GregorianCalendar(2021, 3 - 1, 13).getTime()).withPubliclyAvailable(true)
				.withTitle("a \"title\"").withContent("some content with an antislash: \\").build();
		PostInput inputPost = PostInput.builder().withTopicId("22").withInput(topicPostInput).build();
		//
		Map<String, Object> params = new HashMap<>();
		params.put("post", inputPost);
		params.put("anIntParam", 666);// This

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		assertEquals(
				"{\"query\":\"mutation crPst($post:PostInput!,$anIntParam:Int){createPost(post:$post){id date author{id __typename} __typename}}\",\"variables\":"//
						+ "{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a \\\"title\\\"\",\"content\":\"some content with an antislash: \\\\\"}},"
						+ "\"anIntParam\":666}}",
				graphQLRequest.buildRequestAsString(params));

		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "mutation crPst($post:PostInput!,$anIntParam:Int){createPost(post:$post){id date author{id __typename} __typename}}",
				"{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a \\\"title\\\"\",\"content\":\"some content with an antislash: \\\\\"}},"
						+ "\"anIntParam\":666}", //
				null);
	}

	@Test
	void testBuild_withNameAndGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(
				"query titi($post: PostInput!, $anIntParam: Int, $aCustomScalar : [ [   Date ! ]] !, $anEnum: MemberType ){boards{topics{id}}}");
		CustomScalarRegistryInitializer.initCustomScalarRegistry();
		TopicPostInput topicPostInput = TopicPostInput.builder().withAuthorId("12")
				.withDate(new GregorianCalendar(2021, 3 - 1, 13).getTime()).withPubliclyAvailable(true)
				.withTitle("a title").withContent("some content").build();
		PostInput inputPost = PostInput.builder().withTopicId("22").withInput(topicPostInput).build();
		//
		List<List<Date>> dates = Arrays.asList(
				Arrays.asList(new GregorianCalendar(2021, 4 - 1, 1).getTime(),
						new GregorianCalendar(2021, 4 - 1, 2).getTime()),
				Arrays.asList(new GregorianCalendar(2021, 4 - 1, 3).getTime(),
						new GregorianCalendar(2021, 4 - 1, 4).getTime()));
		//
		Map<String, Object> params = new HashMap<>();
		params.put("post", inputPost);
		params.put("anIntParam", 666);
		params.put("aCustomScalar", dates);
		params.put("anEnum", MemberType.ADMIN);

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		assertEquals(
				"{\"query\":\"query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType){boards{topics{id __typename} __typename}}\",\"variables\":"//
						+ "{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a title\",\"content\":\"some content\"}},"
						+ "\"aCustomScalar\":[[\"2021-04-01\",\"2021-04-02\"],[\"2021-04-03\",\"2021-04-04\"]],\"anEnum\":\"ADMIN\",\"anIntParam\":666}}",
				graphQLRequest.buildRequestAsString(params));
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(graphQLRequest.buildRequestAsMap(params), ""//
				+ "query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType){boards{topics{id __typename} __typename}}",
				"{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a title\",\"content\":\"some content\"}},"
						+ "\"aCustomScalar\":[[\"2021-04-01\",\"2021-04-02\"],[\"2021-04-03\",\"2021-04-04\"]],\"anEnum\":\"ADMIN\",\"anIntParam\":666}", //
				null);
	}
}
