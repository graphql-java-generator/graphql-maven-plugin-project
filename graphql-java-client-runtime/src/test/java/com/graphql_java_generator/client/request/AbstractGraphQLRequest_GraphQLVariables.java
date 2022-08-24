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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.domain.client.forum.CustomScalarRegistryInitializer;
import com.graphql_java_generator.domain.client.forum.GraphQLRequest;
import com.graphql_java_generator.domain.client.forum.MemberType;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.forum.Query;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_GraphQLVariables {

	Query queryType;
	Map<String, Object> params;

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new Query();

		params = new HashMap<>();
		params.put("memberName", "a member Name");
		params.put("sinceParam", new GregorianCalendar(1900, 10 - 1, 24).getTime());
	}

	@Test
	void testBuild_withGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(null,
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
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), ""//
				+ "mutation crPst($post:PostInput!,$anIntParam:Int){createPost(post:$post){id date author{id __typename} __typename}}",
				params, //
				null);
	}

	@Test
	void testBuild_withNameAndGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(null,
				"query titi($post: PostInput!, $anIntParam: Int, $aCustomScalar : [ [   Date ! ]] !, $anEnum: MemberType, $aDate: Date!) {boards{topics{id}}}");
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
		Date aDate = new GregorianCalendar(2021, 10 - 1, 11).getTime();
		//
		Map<String, Object> params = new HashMap<>();
		params.put("post", inputPost);
		params.put("anIntParam", 666);
		params.put("aCustomScalar", dates);
		params.put("anEnum", MemberType.ADMIN);
		params.put("aDate", aDate);

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), ""//
				+ "query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType,$aDate:Date!){boards{topics{id __typename} __typename}}",
				params, //
				null);
	}
}
