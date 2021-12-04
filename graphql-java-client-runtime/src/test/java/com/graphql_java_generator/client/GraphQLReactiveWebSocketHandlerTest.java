package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.client.GraphQLReactiveWebSocketHandler.MessageType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.domain.client.forum.CustomScalarRegistryInitializer;
import com.graphql_java_generator.domain.client.forum.GraphQLRequest;
import com.graphql_java_generator.domain.client.forum.MemberType;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

public class GraphQLReactiveWebSocketHandlerTest {

	@Test
	void testEncode()
			throws GraphQLRequestPreparationException, JsonProcessingException, GraphQLRequestExecutionException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(
				"query titi($post: PostInput!, $anIntParam: Int, $aCustomScalar : [ [   Date ! ]] !, $anEnum: MemberType, $aDate: Date!) {boards{topics{id}}}");
		GraphQLReactiveWebSocketHandler graphQLReactiveWebSocketHandler = new GraphQLReactiveWebSocketHandler(
				graphQLRequest.getGraphQLObjectMapper());
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

		// First: check of the payload (the request)
		Map<String, Object> payload = graphQLRequest.buildRequestAsMap(params);
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(payload, ""//
				+ "query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType,$aDate:Date!){boards{topics{id __typename} __typename}}",
				"{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a title\",\"content\":\"some content\"}},"
						+ "\"aCustomScalar\":[[\"2021-04-01\",\"2021-04-02\"],[\"2021-04-03\",\"2021-04-04\"]],\"aDate\":\"2021-10-11\",\"anEnum\":\"ADMIN\",\"anIntParam\":666}", //
				null);

		assertEquals("{\"type\":\"error\",\"payload\":" + "{" + //
				"\"variables\":"//
				+ "{\"post\":{\"topicId\":\"22\",\"input\":{\"authorId\":\"12\",\"date\":\"2021-03-13\",\"publiclyAvailable\":true,\"title\":\"a title\",\"content\":\"some content\"}},"
				+ "\"aCustomScalar\":[[\"2021-04-01\",\"2021-04-02\"],[\"2021-04-03\",\"2021-04-04\"]],\"aDate\":\"2021-10-11\",\"anEnum\":\"ADMIN\",\"anIntParam\":666},"
				+ "\"query\":\"query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType,$aDate:Date!){boards{topics{id __typename} __typename}}\""//
				+ "},"//
				+ "\"id\":\"123\"}", graphQLReactiveWebSocketHandler.encode("123", MessageType.ERROR, payload));
	}
}
